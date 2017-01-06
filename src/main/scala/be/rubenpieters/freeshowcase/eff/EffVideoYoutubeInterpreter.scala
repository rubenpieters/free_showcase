package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase.unsafe.YoutubeApi
import be.rubenpieters.freeshowcase.{LiteralSearch, Video, VideoDsl, VideoSearchResult}
import cats.Traverse
import org.atnos.eff.interpret._
import org.atnos.eff.{SideEffect, _}
import cats.implicits._
import org.atnos.eff.eff._
import org.atnos.eff.syntax.eff._
import org.atnos.eff.async._
import org.atnos.eff.interpret._

/**
  * Created by ruben on 6/01/2017.
  */
object EffVideoYoutubeInterpreter {
  type VideoError[A] = Either[Throwable, A]
  type _videoErr[R] = VideoError |= R

  def runVideo[R, U, A](effects: Eff[R, A])(implicit m: Member.Aux[VideoDsl, R, U], ve: _videoErr[U]): Eff[U, A] = {
    val _translate = new Translate[VideoDsl, U] {
      override def apply[X](kv: VideoDsl[X]): Eff[U, X] = kv match {
        case LiteralSearch(literal) =>
          val searchResult = YoutubeApi.youtubeLiteralSearch(literal).map(VideoSearchResult)
          send[VideoError, U, VideoSearchResult](searchResult)

      }
    }
    translate(effects)(_translate)
  }
}
