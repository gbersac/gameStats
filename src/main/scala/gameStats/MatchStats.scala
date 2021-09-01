package gameStats

import scala.io.Source
import scala.xml.XML
import scala.util.{Try,Success,Failure}
import scala.xml.Node

final case class Player(uid: String, firstName: String, lastName: String, stats: Map[String, Int])

object Player {
  def fromXML(uid: String, desc: Node, datas: Map[String, Node]): Either[String, Player] = {
    datas.get(uid) match {
      case None => Left(s"No data for user $uid")
      case Some(data) => 
        for {
          fname <- (desc \\ "First").headOption.map(_.text).toRight(s"No first name for player $uid")
          lname <- (desc \\ "Last").headOption.map(_.text).toRight(s"No last name for player $uid")
          // format: <Stat Type="duel_lost">2</Stat>
          stats <- Try((data \\ "Stat").map(p => (p \@ "Type", p.text.toInt)).toMap)
            .toEither.left.map(_ => s"Invalid player stats for player $uid  ")
        } yield Player(uid, fname, lname, stats)
    }
  }
}

sealed trait TeamSide
case object Home extends TeamSide
case object Away extends TeamSide

object TeamSide {
  def fromString(s: String): Either[String, TeamSide] = 
    if (s.toLowerCase == "home") Right(Home)
    else if (s.toLowerCase == "away") Right(Away)
    else Left("Incorect team side")
}

final case class Team(
  name: String,
  teamSide: TeamSide,
  players: Seq[Player],
  stats: Map[String, Float]
)

object Team {
  def fromXML(desc: Node, data: Node): Either[String, Team] = for {
    name <- (desc \ "Name").headOption.map(_.text).toRight("No team name")
    teamSideStr <- Try((data \@ "Side")).toEither.left.map(_ => "No team side")
    teamSide <- TeamSide.fromString(teamSideStr)
    stats <- Try((data \ "Stat").map(p => (p \@ "Type", p.text.toFloat)).toMap)
        .toEither.left.map(_ => s"Invalid team stats for team $name")

    // parsing player
    descs <- Try((desc \ "Player").map(p => (p \@ "uID", p.toList.head)))
        .toEither.left.map(_ => "Invalid player description")
    datas <- Try((data \\ "MatchPlayer").map(p => (p \@ "PlayerRef", p.toList.head)).toMap) 
        .toEither.left.map(_ => "Invalid player data")
    players <- descs.map(p => Player.fromXML(p._1, p._2, datas)).partitionMap(identity) match {
      case (Nil, rights) => Right(rights)
      case (lefts, _)    => Left(lefts.mkString(" # "))
    }
  } yield Team(name, teamSide, players, stats)
}

final case class MatchStats(team1: Team, team2: Team) {
  def players: Seq[Player] = team1.players ++ team2.players
}

object MatchStats {

  private def findCorrespondingTeamData(t: Node, td1: Node, td2: Node): Either[String, Node] = {
    Try((t \@ "uID", td1 \@ "TeamRef", td2 \@ "TeamRef")) match {
      case Success((ut, utd1, utd2)) =>
        if (ut == utd1) Right(td1)
        else if (ut == utd2) Right(td2)
        else Left("Team uid mismatch")
      case _ => 
        Left("Missing team uid")
    }
  }

  // TODO replace the String by an error type
  def fromXML(path: String): Either[String, MatchStats] = {
    Try(Source.fromFile(path)) match {
      case Failure(exception) => 
        Left(s"File $path does not exist")
      case Success(value) => 
        val xml = XML.loadString(value.getLines.mkString)
        ((xml \\ "Team").toList, (xml \\ "TeamData").toList) match {
          case (t1 :: t2 :: Nil, td1 :: td2 :: Nil) =>
            for {
              teamData1 <- findCorrespondingTeamData(t1, td1, td2)
              teamData2 <- findCorrespondingTeamData(t2, td1, td2)
              team1 <- Team.fromXML(t1, teamData1)
              team2 <- Team.fromXML(t2, teamData2)
            } yield MatchStats(team1, team2)
          case _ => Left("Invalid xml")
        }
    }
  }
}
