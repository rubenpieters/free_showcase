package be.rubenpieters.freeshowcase.freek

import be.rubenpieters.freeshowcase._
import cats.free.Free
import cats.implicits._
import _root_.freek._

/**
  * Created by ruben on 8/01/17.
  */
object FreekAppOps {
  type VideoPlaylistApp = VideoDsl :|: PlaylistDsl :|: NilDSL
  val VideoPlaylistApp = DSL.Make[VideoPlaylistApp]

  type FreekApp = MusicDsl :|: VideoPlaylistApp
  val FreekApp = DSL.Make[FreekApp]

  def createPlaylistFromFavoriteTracks(user: String): Free[FreekApp.Cop, Playlist] = for {
    tracks <- FavoriteTracksForUser(user).freek[FreekApp]
    trackSearchTerms = tracks.map(track => s"${track.artist} - ${track.title}")
    playlist <- createPlaylistFromLiteralList(trackSearchTerms).expand[FreekApp]
  } yield playlist

  def createPlaylistFromLiteralList(list: List[String]): Free[VideoPlaylistApp.Cop, Playlist] = for {
    searchResults <- list.traverseU(lit => LiteralSearch(lit).freek[VideoPlaylistApp])
    newPlaylist <- CreatePlaylist.freek[VideoPlaylistApp]
    updatedPlaylist <- searchResults.traverseU(searchResult => AddVideo(searchResult.results.head, newPlaylist).freek[VideoPlaylistApp])
  } yield newPlaylist
}
