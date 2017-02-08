package be.rubenpieters.freeshowcase.main

import be.rubenpieters.freeshowcase.catsfree._
import cats._
import cats.implicits._
import cats.arrow.FunctionK

/**
  * Created by ruben on 13/01/17.
  */
object CatsMain {
  def main(args: Array[String]) = {
    val videosInterp = new CatsVideoYoutubeInterpreter
    val idToRight = λ[FunctionK[Id, Either[Throwable, ?]]](x => Right(x))
    val linkInterp = new CatsPlaylistLinkInterpreter
    val playlistInterp = linkInterp.andThen(idToRight)
    val musicInterp = new CatsMusicLastfmInterpreter
    val setlistInterp = new CatsSetlistFmInterpreter
    val interp = CatsAppOps.mkInterp(playlistInterp, videosInterp, musicInterp, setlistInterp)

//    val result = new CatsAppOps[CatsAppOps.CatsApp].createPlaylistFromFavoriteTracks("rubenpieters").foldMap(interp)
    val result = CatsAppOps.createPlaylistFromArtistSetlist[CatsAppOps.CatsApp]("Lantlôs").foldMap(interp)
    println(result)
    result.fold(_ => println("err"), pl => println(linkInterp.playlistByName(pl.id)))
  }
}
