package be.rubenpieters.freeshowcase.freek

import be.rubenpieters.freeshowcase._
import cats.free.Free
import cats.implicits._
import _root_.freek._

/**
  * Created by ruben on 8/01/17.
  */
object FreekAppOps {
  type FreekApp = VideoDsl :|: PlaylistDsl :|: MusicDsl :|: NilDSL
  val FreekApp = DSL.Make[FreekApp]

  def createPlaylistFromFavoriteTracks(user: String): Free[FreekApp.Cop, Playlist] = for {
    tracks <- FavoriteTracksForUser(user).freek[FreekApp]
    trackSearchTerms = tracks.map(track => s"${track.artist} - ${track.title}")
    playlist <- createPlaylistFromLiteralList(trackSearchTerms)
  } yield playlist

  def createPlaylistFromLiteralList(list: List[String]): Free[FreekApp.Cop, Playlist] = for {
    searchResults <- list.traverseU(lit => LiteralSearch(lit).freek[FreekApp])
    newPlaylist <- CreatePlaylist.freek[FreekApp]
    updatedPlaylist <- searchResults.traverseU(searchResult => AddVideo(searchResult.results.head, newPlaylist).freek[FreekApp])
  } yield newPlaylist
}
