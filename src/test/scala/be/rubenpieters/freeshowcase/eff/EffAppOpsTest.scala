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

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val searchResultMap = Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i)))
    val playlists = mutable.Map[String, (Playlist, List[String])]()

    val result = EffMusicOps.testRunMusic(Map("user" -> List(Track("a", "b"), Track("c", "d"))))(
      EffVideoOps.testRunVideo(searchResultMap)(
      EffPlaylistOps.testRunPlaylist(playlists)(
        EffAppOps.createPlaylistFromFavoriteTracks[Fx.fx3[PlaylistDsl, VideoDsl, MusicDsl]]("user"))))
          .run

    playlists(result.url)._2 shouldEqual List("a", "1")
  }
}
