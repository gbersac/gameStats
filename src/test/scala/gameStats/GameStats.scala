package gameStats

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameStatSpec extends AnyFlatSpec with Matchers {

  val matchs = MatchStats.fromXML("./doc/xml-test.xml").getOrElse(throw new Exception("wrong xml"))

  "The stat calculator" should "return an error for an unknown stat" in {
    GameStat(matchs, "error") shouldEqual Left(s"No player has stat error")
  }

  "The stat calculator" should "return the list of 5 best player for stat fwd_pass" in {
    val s = "fwd_pass"
    GameStat(matchs, s).right.get.bestPlayers.map(t => t._2.stats(s)) shouldEqual Seq(16, 14, 14, 13, 12)
  }

  "The stat calculator" should "return the the stat for each team for fwd_pass" in {
    val s = "fwd_pass"
    GameStat(matchs, s).right.get.statTeam1._1 shouldEqual 82
    GameStat(matchs, s).right.get.statTeam2._1 shouldEqual 76
  }
  
}
