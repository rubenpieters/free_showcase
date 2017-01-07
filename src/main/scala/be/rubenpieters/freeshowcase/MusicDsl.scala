package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 7/01/17.
  */
sealed trait MusicDsl[A]

case class FavoriteTracksForUser(user: String) extends MusicDsl[List[Track]]

