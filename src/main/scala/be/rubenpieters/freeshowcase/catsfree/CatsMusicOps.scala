package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase._
import cats._
import cats.free.{Free, Inject}

/**
  * Created by ruben on 7/01/17.
  */
class CatsMusicOps[F[_]](implicit I: Inject[MusicDsl, F]) {
  def favoriteTracksForUser(user: String) = Free.inject[MusicDsl, F](FavoriteTracksForUser(user))
}

object CatsMusicOps {
  implicit def musicOps[F[_]](implicit I: Inject[MusicDsl, F]): CatsMusicOps[F] = new CatsMusicOps[F]
}

class TestCatsMusicInterp(tracks: Map[String, List[Track]]) extends (MusicDsl ~> Id) {

  override def apply[A](fa: MusicDsl[A]): Id[A] = fa match {
    case FavoriteTracksForUser(user) =>
      tracks.getOrElse(user, List())
  }
}