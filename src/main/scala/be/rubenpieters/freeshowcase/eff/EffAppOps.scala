package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase.{Playlist, PlaylistDsl, VideoDsl}
import be.rubenpieters.freeshowcase.eff.EffPlaylistOps._playlist
import be.rubenpieters.freeshowcase.eff.EffVideoOps._video
import be.rubenpieters.freeshowcase.eff.{EffPlaylistOps => P}
import be.rubenpieters.freeshowcase.eff.{EffVideoOps => V}
import cats.data.{State, Writer}
import cats.implicits._
import org.atnos.eff._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._

/**
  * Created by ruben on 6/01/2017.
  */
object EffAppOps {

  def createPlaylistFromLiteralList[R : _video : _playlist](list: List[String]): Eff[R, Playlist] =
    for {
      searchResults <- list.traverseU(lit => V.literalSearch(lit))
      newPlaylist <- P.createPlaylist()
      updatedPlaylist <- searchResults.traverseU(searchResult => P.addVideo(searchResult.results.head, newPlaylist))
    } yield newPlaylist

}
