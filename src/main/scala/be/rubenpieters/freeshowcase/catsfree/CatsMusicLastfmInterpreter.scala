package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase._
import be.rubenpieters.freeshowcase.unsafe.{LastfmApi, YoutubeApi}
import cats._

/**
  * Created by ruben on 7/01/17.
  */
class CatsMusicLastfmInterpreter extends (MusicDsl ~> Either[Throwable, ?]) {
  override def apply[A](fa: MusicDsl[A]): Either[Throwable, A] = fa match {
    case FavoriteTracksForUser(user) =>
      LastfmApi.lastfmUserLovedTracks(user)
  }
}
