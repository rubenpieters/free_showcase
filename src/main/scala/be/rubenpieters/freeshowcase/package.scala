package be.rubenpieters

/**
  * Created by ruben on 8/02/17.
  */
package object freeshowcase {
  // Search DSL related
  type SearchLiteral = String
  type SearchTerms = List[String]

  // Music DSL related
  type ArtistName = String
  type TrackName = String

  type UserName = String

  // Playlist DSL related
  type PlaylistId = String

  // Video related
  type VideoId = String
  type VideoTitle = String
  type VideoUrl = String
}
