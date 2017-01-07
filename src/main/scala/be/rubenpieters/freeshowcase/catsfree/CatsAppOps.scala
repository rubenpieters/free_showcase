package be.rubenpieters.freeshowcase.catsfree

import cats._
import cats.implicits._
import be.rubenpieters.freeshowcase.{MusicDsl, Playlist, PlaylistDsl, VideoDsl}
import cats.data.Coproduct
import cats.free.Free

/**
  * Created by ruben on 5/01/17.
  */
class CatsAppOps[F[_]](implicit P: CatsPlaylistOps[F], V: CatsVideoOps[F], M: CatsMusicOps[F]) {
  def createPlaylistFromFavoriteTracks(user: String): Free[F, Playlist] = for {
    tracks <- M.favoriteTracksForUser(user)
    trackSearchTerms = tracks.map(track => s"${track.artist} - ${track.title}")
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromLiteralList(list: List[String]): Free[F, Playlist] = for {
    searchResults <- list.traverseU(V.literalSearch)
    newPlaylist <- P.createPlaylist()
    updatedPlaylist <- searchResults.traverseU(searchResult => P.addVideo(searchResult.results.head, newPlaylist))
  } yield newPlaylist
}

object CatsAppOps {
  type CatsApp0[A] = Coproduct[PlaylistDsl, VideoDsl, A]
  type CatsApp[A] = Coproduct[MusicDsl, CatsApp0, A]

  def mkInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F, musicInterp: MusicDsl ~> F): CatsApp ~> F = {
    val interp0: CatsApp0 ~> F = playlistInterp or videoInterp
    val interp: CatsApp ~> F = musicInterp or interp0
    interp
  }
}
