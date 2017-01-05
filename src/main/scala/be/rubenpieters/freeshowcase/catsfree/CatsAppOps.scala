package be.rubenpieters.freeshowcase.catsfree

import cats._
import cats.implicits._
import be.rubenpieters.freeshowcase.{Playlist, PlaylistDsl, VideoDsl}
import cats.data.Coproduct
import cats.free.Free

/**
  * Created by ruben on 5/01/17.
  */
class CatsAppOps[F[_]](implicit P: CatsPlaylistOps[F], V: CatsVideoOps[F]) {

  def createPlaylistFromLiteralList(list: List[String]): Free[F, Playlist] = for {
    searchResults <- list.traverseU(V.literalSearch)
    newPlaylist <- P.createPlaylist()
    updatedPlaylist <- searchResults.traverseU(searchResult => P.addVideo(searchResult.results.head.url, newPlaylist))
  } yield newPlaylist
}

object CatsAppOps {
  type CatsApp[A] = Coproduct[PlaylistDsl, VideoDsl, A]

  def mkInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F): CatsApp ~> F = {
    val interp: CatsApp ~> F = playlistInterp or videoInterp
    interp
  }
}
