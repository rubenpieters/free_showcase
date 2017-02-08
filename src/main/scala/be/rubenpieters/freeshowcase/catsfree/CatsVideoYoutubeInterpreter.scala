package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.unsafe.YoutubeApi
import be.rubenpieters.freeshowcase.{LiteralSearch, VideoDsl, VideoSearchResult}
import cats.~>

/**
  * Created by ruben on 5/01/2017.
  */
class CatsVideoYoutubeInterpreter extends (VideoDsl ~> Either[Throwable, ?]) {
  override def apply[A](fa: VideoDsl[A]): Either[Throwable, A] = fa match {
    case LiteralSearch(literal) =>
      YoutubeApi.youtubeLiteralSearch(literal).map(VideoSearchResult(_))
  }
}
