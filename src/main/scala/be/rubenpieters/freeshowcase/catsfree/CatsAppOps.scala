package be.rubenpieters.freeshowcase.catsfree

import cats._
import cats.implicits._
import be.rubenpieters.freeshowcase._
import cats.data.Coproduct
import cats.free.Free

/**
  * Created by ruben on 5/01/17.
  */
object CatsAppOps {
  def createPlaylistFromFavoriteTracks[F[_]](user: String)(implicit
                                                           P: CatsPlaylistOps[F]
                                                           , V: CatsVideoOps[F]
                                                           , M: CatsMusicOps[F]
                                                           , L: CatsLogOps[F]
                                                           , S: CatsSetlistOps[F]
  ): Free[F, Playlist] = for {
    tracks <- M.favoriteTracksForUser(user)
    trackSearchTerms = tracks.map(track => List(track.artist, track.title))
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromArtistSetlist[F[_]](artist: String)(implicit
                                                            P: CatsPlaylistOps[F]
                                                            , V: CatsVideoOps[F]
                                                            , M: CatsMusicOps[F]
                                                            , L: CatsLogOps[F]
                                                            , S: CatsSetlistOps[F]
  ): Free[F, Playlist] = for {
    tracks <- S.getSetlistTracksForArtist(artist)
    trackSearchTerms = tracks.map(track => List(track.artist, track.title))
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromLiteralList[F[_]](list: List[List[String]])(implicit
                                                                    P: CatsPlaylistOps[F]
                                                                    , V: CatsVideoOps[F]
                                                                    , L: CatsLogOps[F]
  ): Free[F, Playlist] = for {
    searchResults <- list.traverseU(r => V.literalSearch(r.mkString(" - ")))
    _ <- searchResults.traverseU_(r => r.results.traverseU_(v => L.log(s"received search result: ${v.title}")))
    relevantSearchResults = searchResults.zip(list).map{ case (videoSearchResult, terms) => VideoSearchResult(Video.applyMetric(terms.mkString(" - "), videoSearchResult.results))}
    newPlaylist <- P.createPlaylist()
    _ <- relevantSearchResults.traverseU_(r => r.results.traverseU_(v => L.log(s"kept relevant result: ${v.title}")))
    updatedPlaylist <- relevantSearchResults.traverseU(searchResult => searchResult.results match {
      case head :: tail => P.addVideo(head, newPlaylist)
      case _ => Free.pure[F, Either[PlaylistDslError, Unit]](Right(()))
    })
  } yield newPlaylist

  type CatsApp0[A] = Coproduct[PlaylistDsl, VideoDsl, A]
  type CatsApp1[A] = Coproduct[MusicDsl, CatsApp0, A]
  type CatsApp2[A] = Coproduct[LogDsl, CatsApp1, A]
  type CatsApp[A] = Coproduct[SetlistDsl, CatsApp2, A]

  def mkInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F, musicInterp: MusicDsl ~> F
                     , logInterp: LogDsl ~> F, setlistInterp: SetlistDsl ~> F): CatsApp ~> F = {
    val interp0: CatsApp0 ~> F = playlistInterp or videoInterp
    val interp1: CatsApp1 ~> F = musicInterp or interp0
    val interp2: CatsApp2 ~> F = logInterp or interp1
    val interp: CatsApp ~> F = setlistInterp or interp2
    interp
  }

  type PlaylistVideoApp[A] = Coproduct[PlaylistDsl, VideoDsl, A]
  type PlaylistVideoLogApp[A] = Coproduct[LogDsl, PlaylistVideoApp, A]

  def mkPlaylistVideoLogInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F, logInterp: LogDsl ~> F): PlaylistVideoLogApp ~> F = {
    val interp0: PlaylistVideoApp ~> F = playlistInterp or videoInterp
    val interp: PlaylistVideoLogApp ~> F = logInterp or interp0
    interp
  }
}