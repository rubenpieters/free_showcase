package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 7/01/17.
  */
sealed trait MusicDsl[A]

case class FavoriteTracksForUser(user: UserName) extends MusicDsl[List[Track]]

