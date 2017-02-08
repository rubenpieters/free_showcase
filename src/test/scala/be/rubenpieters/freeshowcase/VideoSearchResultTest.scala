package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 8/02/2017.
  */
class VideoSearchResultTest extends TestSpec {
  "reorderResults" should "return most relevant search at the head for case 1" in {
    val searchResults = VideoSearchResult(List(
      "Lantlôs - Pulse/Surreal"
      ,"Lantlos Pulse/Surreal Summerbreeze 2015"
      ,"Lantlôs - \"Pulse/Surreal\""
      ,"Lantlôs - Pulse/Surreal (Vocal Cover)"
      ,"Lantlôs - Intrauterin (with Neige) || live @ Patronaat / Roadburn || 18-04-2013"
      ,"Lantlôs - Pulse Surreal - Violão Cover - Introdução"
      ,"Lantlôs - White Miasma Subtitulado Español"
      ,"Lantlôs - Coma"
    ).map(Video(_, "", "")))

    VideoSearchResult.reorderResults(Track("Lantlôs", "Pulse/Surreal"), searchResults).results.headOption shouldEqual
      Some(Video("Lantlôs - Pulse/Surreal", "", ""))
  }
}
