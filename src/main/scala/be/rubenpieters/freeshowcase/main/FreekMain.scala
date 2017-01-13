package be.rubenpieters.freeshowcase.main

import be.rubenpieters.freeshowcase.catsfree.{CatsAppOps, CatsMusicLastfmInterpreter, CatsPlaylistLinkInterpreter, CatsVideoYoutubeInterpreter}
import be.rubenpieters.freeshowcase.freek.FreekAppOps
import cats._
import cats.implicits._
import cats.arrow.FunctionK
import freek._

/**
  * Created by ruben on 13/01/17.
  */
object FreekMain {
  def main(args: Array[String]) = {
    val videosInterp = new CatsVideoYoutubeInterpreter
    val idToRight = λ[FunctionK[Id, Either[Throwable, ?]]](x => Right(x))
    val linkInterp = new CatsPlaylistLinkInterpreter
    val playlistInterp = linkInterp.andThen(idToRight)
    val musicInterp = new CatsMusicLastfmInterpreter
    val interp = playlistInterp :&: videosInterp :&: musicInterp

    val result = FreekAppOps.createPlaylistFromFavoriteTracks("rubenpieters").interpret(interp)
    println(result)
    result.fold(_ => println("err"), pl => println(linkInterp.playlistByName(pl.url)))
  }
}
