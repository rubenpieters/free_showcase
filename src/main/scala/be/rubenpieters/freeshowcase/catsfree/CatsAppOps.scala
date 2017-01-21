package be.rubenpieters.freeshowcase.catsfree

import cats._
import cats.implicits._
import be.rubenpieters.freeshowcase._
import cats.data.Coproduct
import cats.free.Free

/**
  * Created by ruben on 5/01/17.
  */
class CatsAppOps[F[_]](implicit P: CatsPlaylistOps[F], V: CatsVideoOps[F], M: CatsMusicOps[F], L: CatsLogOps[F]) {
  def createPlaylistFromFavoriteTracks(user: String): Free[F, Playlist] = for {
    tracks <- M.favoriteTracksForUser(user)
    trackSearchTerms = tracks.map(track => List(track.artist, track.title))
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromLiteralList(list: List[List[String]]): Free[F, Playlist] = for {
    searchResults <- list.traverseU(r => V.literalSearch(r.mkString(" - ")))
    _ <- searchResults.traverseU_(r => r.results.traverseU_(v => L.log(s"received search result: ${v.title}")))
    relevantSearchResults = searchResults.zip(list).map{ case (videoSearchResult, terms) => VideoSearchResult(videoSearchResult.results.filter(t => Video.containsTermsFully(t.title, terms)))}
    newPlaylist <- P.createPlaylist()
    _ <- relevantSearchResults.traverseU_(r => r.results.traverseU_(v => L.log(s"kept relevant result: ${v.title}")))
    updatedPlaylist <- relevantSearchResults.traverseU(searchResult => searchResult.results match {
      case head :: tail => P.addVideo(head, newPlaylist)
      case _ => Free.pure[F, Either[PlaylistDslError, Unit]](Right(()))
    })
  } yield newPlaylist
}

object CatsAppOps {
  type CatsApp0[A] = Coproduct[PlaylistDsl, VideoDsl, A]
  type CatsApp1[A] = Coproduct[MusicDsl, CatsApp0, A]
  type CatsApp[A] = Coproduct[LogDsl, CatsApp1, A]

  def mkInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F, musicInterp: MusicDsl ~> F, logInterp: LogDsl ~> F): CatsApp ~> F = {
    val interp0: CatsApp0 ~> F = playlistInterp or videoInterp
    val interp1: CatsApp1 ~> F = musicInterp or interp0
    val interp: CatsApp ~> F = logInterp or interp1
    interp
  }
}
