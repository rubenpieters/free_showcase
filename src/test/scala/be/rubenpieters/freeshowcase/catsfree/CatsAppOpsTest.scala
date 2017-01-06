package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.{PlaylistDsl, Video}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 5/01/17.
  */
class CatsAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("test" -> List("a", "b", "c"), "test2" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i))))
    val playlistInterp = new TestCatsPlaylistInterp()
    val interp = CatsAppOps.mkInterp(playlistInterp, videosInterp)

    val result = new CatsAppOps[CatsAppOps.CatsApp].createPlaylistFromLiteralList(List("test", "test2")).foldMap(interp)
    println(result)
    val videos = new CatsPlaylistOps[PlaylistDsl].getVideos(result).foldMap(playlistInterp)
    println(videos)
    videos shouldEqual Right(List("a", "1"))
  }


}
