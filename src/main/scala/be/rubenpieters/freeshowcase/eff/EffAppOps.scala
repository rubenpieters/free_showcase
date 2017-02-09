package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase.eff.EffMusicOps._music
import be.rubenpieters.freeshowcase._
import be.rubenpieters.freeshowcase.eff.EffPlaylistOps._playlist
import be.rubenpieters.freeshowcase.eff.EffSetlistOps._setlist
import be.rubenpieters.freeshowcase.eff.EffVideoOps._video
import be.rubenpieters.freeshowcase.eff.{EffPlaylistOps => P}
import be.rubenpieters.freeshowcase.eff.{EffVideoOps => V}
import be.rubenpieters.freeshowcase.eff.{EffMusicOps => M}
import be.rubenpieters.freeshowcase.eff.{EffSetlistOps => S}
import cats.implicits._
import org.atnos.eff._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._

/**
  * Created by ruben on 6/01/2017.
  */
object EffAppOps {
  def createPlaylistFromFavoriteTracks[R : _video : _playlist : _music](user: UserName): Eff[R, Playlist] = for {
    tracks <- M.favoriteTracksForUser(user)
    playlist <- createPlaylistFromSearchableList(tracks)
  } yield playlist

  def createPlaylistFromArtistSetlist[R : _video : _playlist : _setlist](artist: ArtistName): Eff[R, Playlist] = for {
    tracks <- S.getSetlistTracksForArtist(artist)
    playlist <- createPlaylistFromSearchableList(tracks)
  } yield playlist

  def createPlaylistFromSearchableList[R : _video : _playlist, A: Searchable](list: List[A]): Eff[R, Playlist] = for {
    searchResults <- list.traverseU(term => V.literalSearchableSearch(term).map(result => (term, result)))
    mostRelevantResults = searchResults.flatMap{ case (term, result) => VideoSearchResult.mostRelevantResult(term, result)}
    newPlaylist <- P.createPlaylist()
    _ <- mostRelevantResults.traverseU(P.addVideo(_, newPlaylist))
  } yield newPlaylist
}
