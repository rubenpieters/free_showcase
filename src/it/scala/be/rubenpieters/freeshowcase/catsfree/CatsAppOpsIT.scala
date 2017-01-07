package be.rubenpieters.freeshowcase.catsfree

import cats._
import cats.implicits._
import cats.arrow.FunctionK
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 6/01/2017.
  */
class CatsAppOpsIT extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val videosInterp = new CatsVideoYoutubeInterpreter
    val idToRight = λ[FunctionK[Id, Either[Throwable, ?]]](x => Right(x))
    val linkInterp = new CatsPlaylistLinkInterpreter
    val playlistInterp = linkInterp.andThen(idToRight)
    val musicInterp = new CatsMusicLastfmInterpreter
    val interp = CatsAppOps.mkInterp(playlistInterp, videosInterp, musicInterp)

    val result = new CatsAppOps[CatsAppOps.CatsApp].createPlaylistFromFavoriteTracks("rubenpieters").foldMap(interp)
    println(result)
    result.fold(_ => println("err"), pl => println(linkInterp.playlistByName(pl.url)))
  }
}
