package be.rubenpieters.freeshowcase

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 22/01/17.
  */
class StringSimilarityTest extends FlatSpec with Matchers {
  "similarityThreshold" should "only keep relevant results for case 1" in {
    val comparison = "Lantlôs - Pulse/Surreal"
    val input = List(
      ("Lantlôs - Pulse/Surreal", true)
      ,("Lantlos Pulse/Surreal Summerbreeze 2015", true)
      ,("Lantlôs - \"Pulse/Surreal\"", true)
      ,("Lantlôs - Pulse/Surreal (Vocal Cover)", true)
      ,("Lantlôs - Intrauterin (with Neige) || live @ Patronaat / Roadburn || 18-04-2013", false)
      ,("Lantlôs - Pulse Surreal - Violão Cover - Introdução", true)
      ,("Lantlôs - White Miasma Subtitulado Español", true)
      ,("Lantlôs - Coma", true) // we don't actually want this result as relevant, maybe the metric could be tweaked to handle this
    )

    checkRelevantMatches(comparison, input)
  }

  "similarityThreshold" should "only keep relevant results for case 2" in {
    val comparison = "Maybeshewill - He Films the Clouds Pt. 2"
    val input = List(
      ("Maybeshewill - He Films the Clouds, Pt. 2", true)
      ,("Maybeshewill-He Films The Clouds Part 2 ( with Lyrics)", true)
      ,("Maybeshewill - He films the clouds Pt. 2", true)
      ,("This is Football - Best Moments 2011_2012", false)
      ,("He Films The Clouds Pt. 2", false)
      ,("Maybeshewill - \"He Films the Clouds\" (Parts 1 + 2)", true)
      ,("Dedication Towards Sports", false)
      ,("Maybeshewill-He films clouds pt 2", true)
      ,("Football Emotions - It's all about Respect", false)
      ,("Maybeshewill - He Films The Clouds Pt. 2", true)
    )

    checkRelevantMatches(comparison, input)
  }

  "similarityThreshold" should "only keep relevant results for case 3" in {
    val comparison = "Darren Korb - In Circles"
    val input = List(
      ("Transistor OST - In Circles (feat Ashley Barrett)", false)
      ,("Transistor Original Soundtrack - In Circles", false)
      ,("Darren Korb & Ashley Barrett Live @ PAX Prime 2013", true)
      ,("Darren Korb feat. Ashley Barrett - In Circles (Transistor - OST) RM-№2", true)
      ,("Transistor - In Circles Cover", false)
      ,("Transistor - In Circles Remix", false)
      ,("Transistor OST - In Circles", false)
      ,("Transistor Original Soundtrack Extended - _n C_rcl_s", false)
      ,("Osu! - In Circles - Darren Korb - Hard 4K", false)
      ,("Transistor OST - In Circles (Sub Esp)", false)
    )

    checkRelevantMatches(comparison, input)
  }

  def checkRelevantMatches(comparison: String, input: List[(String, Boolean)]) = {
    val output = input.map { case (str, matchBool) =>
      println(str + " --- " + Video.metric.compare(str, comparison))
      (Video.metric.compare(str, comparison) >= Video.similarityThreshold) == matchBool
    }
    output shouldEqual List.fill(input.size)(true)
  }
}
