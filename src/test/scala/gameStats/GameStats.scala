package gameStats

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameStatSpec extends AnyFlatSpec with Matchers {

  val matchs = MatchStats.fromXML("./doc/xml-test.xml").getOrElse(throw new Exception("wrong xml"))

  "The stat calculator" should "return an error for an unknown stat" in {
    SelectedStats(matchs, "error") shouldEqual Left(s"No player has stat error")
  }

  "The stat calculator" should "return the list of 5 best player for stat fwd_pass" in {
    val s = "fwd_pass"
    SelectedStats(matchs, s).right.get.bestPlayers.map(t => t._2.stats(s)) shouldEqual Seq(16, 14, 14, 13, 12)
  }

  "The stat calculator" should "return the the stat for each team for fwd_pass" in {
    val s = "fwd_pass"
    SelectedStats(matchs, s).right.get.statTeam1._1 shouldEqual 82
    SelectedStats(matchs, s).right.get.statTeam2._1 shouldEqual 76
  }

  "The result XML" should "be correct" in {
    val s = "fwd_pass"
    val stats = SelectedStats(matchs, s).right.get
    scala.xml.Utility.trim(stats.toXML).toString shouldEqual scala.xml.Utility.trim({
      <RESULT>
        <PLAYER>
          <POSITION_IN_RANKING>1</POSITION_IN_RANKING>
          <FIRST_NAME>Federico</FIRST_NAME>
          <LAST_NAME>Fernandez</LAST_NAME>
          <STATISTIC_VALUE>16</STATISTIC_VALUE>
        </PLAYER><PLAYER>
          <POSITION_IN_RANKING>2</POSITION_IN_RANKING>
          <FIRST_NAME>Phil</FIRST_NAME>
          <LAST_NAME>Jagielka</LAST_NAME>
          <STATISTIC_VALUE>14</STATISTIC_VALUE>
        </PLAYER><PLAYER>
          <POSITION_IN_RANKING>3</POSITION_IN_RANKING>
          <FIRST_NAME>Kyle</FIRST_NAME>
          <LAST_NAME>Naughton</LAST_NAME>
          <STATISTIC_VALUE>14</STATISTIC_VALUE>
        </PLAYER><PLAYER>
          <POSITION_IN_RANKING>4</POSITION_IN_RANKING>
          <FIRST_NAME>Jordan</FIRST_NAME>
          <LAST_NAME>Pickford</LAST_NAME>
          <STATISTIC_VALUE>13</STATISTIC_VALUE>
        </PLAYER><PLAYER>
          <POSITION_IN_RANKING>5</POSITION_IN_RANKING>
          <FIRST_NAME>Morgan</FIRST_NAME>
          <LAST_NAME>Schneiderlin</LAST_NAME>
          <STATISTIC_VALUE>12</STATISTIC_VALUE>
        </PLAYER>
        <TEAM>
            <TEAM_SIDE>Home</TEAM_SIDE>
            <TEAM_NAME>Swansea City</TEAM_NAME>
            <SUM_OF_STATISTIC_VALUES>82.0</SUM_OF_STATISTIC_VALUES>
        </TEAM>
        <TEAM>
            <TEAM_SIDE>Away</TEAM_SIDE>
            <TEAM_NAME>Everton</TEAM_NAME>
            <SUM_OF_STATISTIC_VALUES>76.0</SUM_OF_STATISTIC_VALUES>
        </TEAM>
      </RESULT>
    }).toString
  }
  
}
