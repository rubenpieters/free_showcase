package be.rubenpieters.freeshowcase.catsfree

import cats._
import cats.implicits._
import be.rubenpieters.freeshowcase._
import cats.data.Coproduct
import cats.free.Free
import be.rubenpieters.freeshowcase.Searchable.ops._

/**
  * Created by ruben on 5/01/17.
  */
object CatsAppOps {
  def createPlaylistFromFavoriteTracks[F[_]](user: UserName)(implicit
                                                           P: CatsPlaylistOps[F]
                                                           , V: CatsVideoOps[F]
                                                           , M: CatsMusicOps[F]
                                                           , S: CatsSetlistOps[F]
  ): Free[F, Playlist] = for {
    tracks <- M.favoriteTracksForUser(user)
    trackSearchTerms = tracks.map(track => track.asSearchTerms)
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromArtistSetlist[F[_]](artist: ArtistName)(implicit
                                                            P: CatsPlaylistOps[F]
                                                            , V: CatsVideoOps[F]
                                                            , M: CatsMusicOps[F]
                                                            , S: CatsSetlistOps[F]
  ): Free[F, Playlist] = for {
    tracks <- S.getSetlistTracksForArtist(artist)
    trackSearchTerms = tracks.map(track => List(track.artist, track.title))
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromLiteralList[F[_]](list: List[SearchTerms])(implicit
                                                                    P: CatsPlaylistOps[F]
                                                                    , V: CatsVideoOps[F]
  ): Free[F, Playlist] = for {
    searchResults <- list.traverseU(r => V.literalSearch(r.mkString(" - ")))
    relevantSearchResults = searchResults.zip(list).map{ case (videoSearchResult, terms) => VideoSearchResult(Video.applyMetric(terms.mkString(" - "), videoSearchResult.results))}
    newPlaylist <- P.createPlaylist()
    updatedPlaylist <- relevantSearchResults.traverseU(searchResult => searchResult.results match {
      case head :: tail => P.addVideo(head, newPlaylist)
      case _ => Free.pure[F, Either[PlaylistDslError, Unit]](Right(()))
    })
  } yield newPlaylist

  type CatsApp0[A] = Coproduct[PlaylistDsl, VideoDsl, A]
  type CatsApp1[A] = Coproduct[MusicDsl, CatsApp0, A]
  type CatsApp[A] = Coproduct[SetlistDsl, CatsApp1, A]

  def mkInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F, musicInterp: MusicDsl ~> F
                     , setlistInterp: SetlistDsl ~> F): CatsApp ~> F = {
    val interp0: CatsApp0 ~> F = playlistInterp or videoInterp
    val interp1: CatsApp1 ~> F = musicInterp or interp0
    val interp: CatsApp ~> F = setlistInterp or interp1
    interp
  }

  type PlaylistVideoApp[A] = Coproduct[PlaylistDsl, VideoDsl, A]

  def mkPlaylistVideoLogInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F): PlaylistVideoApp ~> F = {
    val interp: PlaylistVideoApp ~> F = playlistInterp or videoInterp
    interp
  }
}