package gameStats

import scala.xml.Elem

final case class SelectedStats(
  stat: String,
  bestPlayers: Map[Int, Player], 
  statTeam1: (Float, Team), 
  statTeam2: (Float, Team)
) {
  def toXML: Elem = {
    def teamToXml(team: Team, stat: Float) = {
      <TEAM>
        <TEAM_SIDE>{team.teamSide.toString}</TEAM_SIDE>
        <TEAM_NAME>{team.name}</TEAM_NAME>
        <SUM_OF_STATISTIC_VALUES>{stat}</SUM_OF_STATISTIC_VALUES>
      </TEAM>
    }

    <RESULT>
      {bestPlayers.map { case (i, p) => 
        <PLAYER>
          <POSITION_IN_RANKING>{i + 1}</POSITION_IN_RANKING>
          <FIRST_NAME>{p.firstName}</FIRST_NAME> 
          <LAST_NAME>{p.lastName}</LAST_NAME> 
          <STATISTIC_VALUE>{p.stats.get(stat.toString).getOrElse("")}</STATISTIC_VALUE>
        </PLAYER>
      }}
      {teamToXml(statTeam1._2, statTeam1._1)}
      {teamToXml(statTeam2._2, statTeam2._1)}
    </RESULT>
  }
}

object SelectedStats {
  def apply(matchs: MatchStats, stat: String): Either[String, SelectedStats] = for {
    bestPlayers <- {
      val players = matchs.players.filter(p => p.stats.contains(stat))
      if (players.length == 0) Left(s"No player has stat $stat")
      else if (players.length < 5) Left(s"Not enough player has stat $stat")
      else Right (
        players
          .sortBy(p => p.stats.get(stat).getOrElse(-1))
          .reverse
          .take(5)
          .zipWithIndex
          .map(t => (t._2, t._1))
          .toMap
      )
    }
    statTeam1 <- matchs.team1.stats.get(stat).toRight(s"No stat $stat for team ${matchs.team1.name}")
    statTeam2 <- matchs.team2.stats.get(stat).toRight(s"No stat $stat for team ${matchs.team2.name}")
  } yield SelectedStats(
    stat = stat, 
    bestPlayers = bestPlayers, 
    statTeam1 = (statTeam1, matchs.team1), 
    statTeam2 = (statTeam2, matchs.team2)
  )
}

object Main extends App {
  if (args.length < 2)
    println("usage: gameStats statToCkeck path")
  else {
    val statToCheck = args(0)
    val path = args(1)
    MatchStats.fromXML(path).flatMap(m => SelectedStats(m, statToCheck)) match {
      case Left(err) => println(err)
      case Right(stats) => println(stats.toXML.toString())
    }
  }
}
