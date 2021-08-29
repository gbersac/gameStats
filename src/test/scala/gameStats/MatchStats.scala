package gameStats

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// TODO add more test, especially more error tests.
class MatchStatsSpec extends AnyFlatSpec with Matchers {
  "The xml parser" should "fail when the provided path point to a non existing file" in {
    MatchStats.fromXML("wrongPath") shouldEqual Left("File does not exist")
  }

  "The xml parser" should "find the correct team names" in {
    MatchStats.fromXML("./doc/xml-test.xml")
      .map(m => (m.team1.name, m.team2.name)) shouldEqual Right(("Swansea City", "Everton"))
  }

  "The xml parser" should "parse the correct number of player" in {
    MatchStats.fromXML("./doc/xml-test.xml")
      .map(m => (m.team1.players.length, m.team2.players.length)) shouldEqual Right((18, 18))
  }

  "The xml parser" should "parse the correct number of player stats" in {
    MatchStats.fromXML("./doc/xml-test.xml")
      .map(m => m.team1.players.find(p => p.uid == "p37096").get.stats.size) shouldEqual Right(40)
  }

}
