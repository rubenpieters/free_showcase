package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase._
import be.rubenpieters.freeshowcase.catsfree.{CatsAppOps, CatsPlaylistOps, TestCatsPlaylistInterp, TestCatsVideoInterp}
import org.scalatest.{FlatSpec, Matchers}
import org.atnos.eff._
import syntax.all._

import scala.collection.mutable

/**
  * Created by ruben on 6/01/2017.
  */
class EffAppOpsTest extends FlatSpec with Matchers {
  "createPlaylistFromSearchableList" should "create the playlist from literal terms" in {
    val searchResultMap = Map("a - b" -> List("a - b", "b", "c"), "c - d" -> List("c - d", "2", "3")).mapValues(_.map(i => Video(i, i, i)))
    val playlists = mutable.Map[String, (Playlist, List[String])]()

    val result = EffVideoOps.testRunVideo(searchResultMap)(
        EffPlaylistOps.testRunPlaylist(playlists)(
          EffAppOps.createPlaylistFromSearchableList[Fx.fx2[PlaylistDsl, VideoDsl], Track](List(Track("a", "b"), Track("c", "d")))))
      .run

    playlists(result.id)._2 shouldEqual List("a - b", "c - d")
  }

  "createPlaylistFromFavoriteTracks" should "correctly create a playlist" in {
    val searchResultMap = Map("a - b" -> List("a - b", "b", "c"), "c - d" -> List("c - d", "2", "3")).mapValues(_.map(i => Video(i, i, i)))
    val playlists = mutable.Map[String, (Playlist, List[String])]()

    val result = EffMusicOps.testRunMusic(Map("user" -> List(Track("a", "b"), Track("c", "d"))))(
      EffVideoOps.testRunVideo(searchResultMap)(
      EffPlaylistOps.testRunPlaylist(playlists)(
        EffAppOps.createPlaylistFromFavoriteTracks[Fx.fx3[PlaylistDsl, VideoDsl, MusicDsl]]("user"))))
          .run

    playlists(result.id)._2 shouldEqual List("a - b", "c - d")
  }

  "createPlaylistFromArtistSetlist" should "correctly create the playlist" in {
    val searchResultMap = Map("artist - song1" -> List("artist - song1", "b", "c"), "artist - song2" -> List("artist - song2", "2", "3")).mapValues(_.map(i => Video(i, i, i)))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val setlistMap = Map("artist" -> List(Track("artist", "song1"), Track("artist", "song2")))

    val result = EffSetlistOps.testRunSetlist(setlistMap)(
      EffVideoOps.testRunVideo(searchResultMap)(
        EffPlaylistOps.testRunPlaylist(playlists)(
          EffAppOps.createPlaylistFromArtistSetlist[Fx.fx3[PlaylistDsl, VideoDsl, SetlistDsl]]("artist"))))
      .run

    playlists(result.id)._2 shouldEqual List("artist - song1", "artist - song2")
  }
}
