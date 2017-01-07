package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.{PlaylistDsl, Track, Video}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 5/01/17.
  */
class CatsAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i))))
    val playlistInterp = new TestCatsPlaylistInterp()
    val musicInterp = new TestCatsMusicInterp(Map("user" -> List(Track("a", "b"), Track("c", "d"))))
    val interp = CatsAppOps.mkInterp(playlistInterp, videosInterp, musicInterp)

    val result = new CatsAppOps[CatsAppOps.CatsApp].createPlaylistFromFavoriteTracks("user").foldMap(interp)
    println(result)
    val videos = new CatsPlaylistOps[PlaylistDsl].getVideos(result).foldMap(playlistInterp)
    println(videos)
    videos shouldEqual Right(List("a", "1"))
  }


}
