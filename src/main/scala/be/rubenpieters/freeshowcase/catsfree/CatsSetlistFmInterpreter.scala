package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.unsafe.SetlistfmApi
import be.rubenpieters.freeshowcase.{GetSetlistTracksForArtist, SetlistDsl}
import cats.~>

/**
  * Created by ruben on 22/01/17.
  */
class CatsSetlistFmInterpreter extends (SetlistDsl ~> Either[Throwable, ?]) {
  override def apply[A](fa: SetlistDsl[A]): Either[Throwable, A] = fa match {
    case GetSetlistTracksForArtist(artist) =>
      SetlistfmApi.setlistfmSetlistForArtist(artist)
  }
}
