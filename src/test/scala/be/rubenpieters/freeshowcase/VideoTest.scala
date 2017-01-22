package be.rubenpieters.freeshowcase

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 13/01/17.
  */
class VideoTest extends FlatSpec with Matchers {
  "containsTermsFully" should "return true when the title contains all the terms fully" in {
    val title = "abc def ghi"

    Video.containsTermsFully(title, List("abc def", "ghi")) shouldBe true
  }

  "containsTermsFully" should "return false when a term isn't found fully" in {
    val title = "abc def ghi"

    Video.containsTermsFully(title, List("ab def", "ghi")) shouldBe false
  }

  "applyMetric" should "return most relevant search at the head for case 1" in {
    val searchResults = List(
      "Lantlôs - Pulse/Surreal"
      ,"Lantlos Pulse/Surreal Summerbreeze 2015"
      ,"Lantlôs - \"Pulse/Surreal\""
      ,"Lantlôs - Pulse/Surreal (Vocal Cover)"
      ,"Lantlôs - Intrauterin (with Neige) || live @ Patronaat / Roadburn || 18-04-2013"
      ,"Lantlôs - Pulse Surreal - Violão Cover - Introdução"
      ,"Lantlôs - White Miasma Subtitulado Español"
      ,"Lantlôs - Coma"
    ).map(Video(_, "", ""))

    Video.applyMetric("Lantlôs - Pulse/Surreal", searchResults).headOption shouldEqual Some(Video("Lantlôs - Pulse/Surreal", "", ""))
  }
}
