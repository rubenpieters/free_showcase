package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase.{PlaylistDsl, Video, VideoDsl}
import be.rubenpieters.freeshowcase.catsfree.{CatsAppOps, CatsPlaylistOps, TestCatsPlaylistInterp, TestCatsVideoInterp}
import org.scalatest.{FlatSpec, Matchers}
import org.atnos.eff._
import syntax.all._

/**
  * Created by ruben on 6/01/2017.
  */
class EffAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val searchResultMap = Map("test" -> List("a", "b", "c"), "test2" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i)))

    val result = EffVideoOps.testRunVideo(searchResultMap)(
      EffPlaylistOps.testRunPlaylist(
        EffAppOps.createPlaylistFromLiteralList[Fx.fx2[PlaylistDsl, VideoDsl]](List("test", "test2"))))
          .run

    println(result)

    val videos = EffPlaylistOps.testRunPlaylist(EffPlaylistOps.getVideos[Fx.fx1[PlaylistDsl]](result)).run
    println(videos)
    // doesn't work because we cannot reuse the interpreter state
//    videos shouldEqual Right(List("a", "1"))
  }
}
