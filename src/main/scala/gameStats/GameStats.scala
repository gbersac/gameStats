package gameStats

final case class GameStat(
  stat: String,
  bestPlayers: Map[Int, Player], 
  statTeam1: (Float, Team), 
  statTeam2: (Float, Team)
)

object GameStat {
  def apply(matchs: MatchStats, stat: String): Either[String, GameStat] = for {
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
  } yield GameStat(
    stat = stat, 
    bestPlayers = bestPlayers, 
    statTeam1 = (statTeam1, matchs.team1), 
    statTeam2 = (statTeam2, matchs.team2)
  )
}

object Main extends App {

  //path = "doc/xml-test.xml"
  //statToCheck = "total_final_third_passes"
  


}
