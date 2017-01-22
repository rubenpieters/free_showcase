package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase._
import cats._
import cats.free.{Free, Inject}

/**
  * Created by ruben on 22/01/17.
  */
class CatsSetlistOps[F[_]](implicit I: Inject[SetlistDsl, F]) {
  def getSetlistTracksForArtist(artist: String) = Free.inject[SetlistDsl, F](GetSetlistTracksForArtist(artist))
}

object CatsSetlistOps {
  implicit def SetlistOps[F[_]](implicit I: Inject[SetlistDsl, F]): CatsSetlistOps[F] = new CatsSetlistOps[F]
}

class TestCatsSetlistInterp(tracks: Map[String, List[Track]]) extends (SetlistDsl ~> Id) {
  override def apply[A](fa: SetlistDsl[A]): Id[A] = fa match {
    case GetSetlistTracksForArtist(artist) => tracks(artist)
  }
}
