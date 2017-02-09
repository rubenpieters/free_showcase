package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase._
import cats._
import cats.data.Coproduct
import cats.free.Free
import cats.implicits._

/**
  * Created by ruben on 5/01/17.
  */
object CatsAppOps {
  def createPlaylistFromFavoriteTracks[F[_]](user: UserName)(implicit
                                                             P: CatsPlaylistOps[F]
                                                             , V: CatsVideoOps[F]
                                                             , M: CatsMusicOps[F]
  ): Free[F, Playlist] = for {
    tracks <- M.favoriteTracksForUser(user)
    playlist <- createPlaylistFromSearchableList(tracks)
  } yield playlist

  def createPlaylistFromArtistSetlist[F[_]](artist: ArtistName)(implicit
                                                                P: CatsPlaylistOps[F]
                                                                , V: CatsVideoOps[F]
                                                                , S: CatsSetlistOps[F]
  ): Free[F, Playlist] = for {
    tracks <- S.getSetlistTracksForArtist(artist)
    playlist <- createPlaylistFromSearchableList(tracks)
  } yield playlist

  def createPlaylistFromSearchableList[F[_], A: Searchable](list: List[A])(implicit
                                                                           P: CatsPlaylistOps[F]
                                                                           , V: CatsVideoOps[F]
  ): Free[F, Playlist] = for {
    searchResults <- list.traverseU(term => V.literalSearchableSearch(term).map(result => (term, result)))
    mostRelevantResults = searchResults.flatMap{ case (term, result) => VideoSearchResult.mostRelevantResult(term, result)}
    newPlaylist <- P.createPlaylist()
    _ <- mostRelevantResults.traverseU(P.addVideo(_, newPlaylist))
  } yield newPlaylist

  type PlaylistVideoApp[A] = Coproduct[PlaylistDsl, VideoDsl, A]

  def mkPlaylistVideoInterp[F[_]](playlistInterp: PlaylistDsl ~> F, videoInterp: VideoDsl ~> F): PlaylistVideoApp ~> F = {
    val interp: PlaylistVideoApp ~> F = playlistInterp or videoInterp
    interp
  }

  type MusicPlaylistVideoApp[A] = Coproduct[MusicDsl, PlaylistVideoApp, A]

  def mkMusicPlaylistVideoInterp[F[_]](playlistInterp: PlaylistDsl ~> F
                                       , videoInterp: VideoDsl ~> F
                                       , musicInterp: MusicDsl ~> F
                                      ): MusicPlaylistVideoApp ~> F = {
    musicInterp or mkPlaylistVideoInterp(playlistInterp, videoInterp)
  }

  type SetlistPlaylistVideoApp[A] = Coproduct[SetlistDsl, PlaylistVideoApp, A]

  def mkSetlistPlaylistVideoInterp[F[_]](playlistInterp: PlaylistDsl ~> F
                                         , videoInterp: VideoDsl ~> F
                                         , setlistInterp: SetlistDsl ~> F
                                        ): SetlistPlaylistVideoApp ~> F = {
    setlistInterp or mkPlaylistVideoInterp(playlistInterp, videoInterp)
  }
}