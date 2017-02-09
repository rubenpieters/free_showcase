package be.rubenpieters.freeshowcase.freek

import _root_.freek._
import be.rubenpieters.freeshowcase._
import cats.free.Free
import cats.implicits._

/**
  * Created by ruben on 8/01/17.
  */
object FreekAppOps {
  def createPlaylistFromFavoriteTracks(user: UserName): Free[MusicPlaylistVideoApp.Cop, Playlist] = for {
    tracks <- FavoriteTracksForUser(user).freek[MusicPlaylistVideoApp]
    playlist <- createPlaylistFromSearchableList(tracks).expand[MusicPlaylistVideoApp]
  } yield playlist

  def createPlaylistFromArtistSetlist(artist: ArtistName): Free[SetlistPlaylistVideoApp.Cop, Playlist] = for {
    tracks <- GetSetlistTracksForArtist(artist).freek[SetlistPlaylistVideoApp]
    playlist <- createPlaylistFromSearchableList(tracks).expand[SetlistPlaylistVideoApp]
  } yield playlist

  def createPlaylistFromSearchableList[A: Searchable](list: List[A]): Free[PlaylistVideoApp.Cop, Playlist] = for {
      searchResults <- list.traverseU(term => FreekVideoOps.literalSearchableSearch(term).freek[PlaylistVideoApp].map(result => (term, result)))
      mostRelevantResults = searchResults.flatMap{ case (term, result) => VideoSearchResult.mostRelevantResult(term, result)}
      newPlaylist <- CreatePlaylist.freek[PlaylistVideoApp]
      _ <- mostRelevantResults.traverseU(AddVideo(_, newPlaylist).freek[PlaylistVideoApp])
    } yield newPlaylist

  type PlaylistVideoApp = VideoDsl :|: PlaylistDsl :|: NilDSL
  val PlaylistVideoApp = DSL.Make[PlaylistVideoApp]

  type MusicPlaylistVideoApp = MusicDsl :|: PlaylistVideoApp
  val MusicPlaylistVideoApp = DSL.Make[MusicPlaylistVideoApp]

  type SetlistPlaylistVideoApp = SetlistDsl :|: PlaylistVideoApp
  val SetlistPlaylistVideoApp = DSL.Make[SetlistPlaylistVideoApp]
}
