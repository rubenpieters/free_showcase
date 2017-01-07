package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase._
import be.rubenpieters.freeshowcase.catsfree.{CatsAppOps, CatsPlaylistOps, TestCatsPlaylistInterp, TestCatsVideoInterp}
import org.scalatest.{FlatSpec, Matchers}
import org.atnos.eff._
import syntax.all._

/**
  * Created by ruben on 6/01/2017.
  */
class EffAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val searchResultMap = Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i)))

    val result = EffMusicOps.testRunMusic(Map("user" -> List(Track("a", "b"), Track("c", "d"))))(
      EffVideoOps.testRunVideo(searchResultMap)(
      EffPlaylistOps.testRunPlaylist(
        EffAppOps.createPlaylistFromFavoriteTracks[Fx.fx3[PlaylistDsl, VideoDsl, MusicDsl]]("user"))))
          .run

    println(result)

    val videos = EffPlaylistOps.testRunPlaylist(EffPlaylistOps.getVideos[Fx.fx1[PlaylistDsl]](result)).run
    println(videos)
    // doesn't work because we cannot reuse the interpreter state
//    videos shouldEqual Right(List("a", "1"))
  }
}
