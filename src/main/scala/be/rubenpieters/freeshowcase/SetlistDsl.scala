package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 22/01/17.
  */
sealed trait SetlistDsl[A]

case class GetSetlistTracksForArtist(artist: ArtistName) extends SetlistDsl[List[Track]]