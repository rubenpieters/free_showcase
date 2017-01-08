package be.rubenpieters.freeshowcase.freek

import be.rubenpieters.freeshowcase.{PlaylistDsl, Track, Video}
import be.rubenpieters.freeshowcase.catsfree._
import org.scalatest.{FlatSpec, Matchers}
import _root_.freek._

/**
  * Created by ruben on 8/01/17.
  */
class FreekAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i))))
    val playlistInterp = new TestCatsPlaylistInterp()
    val musicInterp = new TestCatsMusicInterp(Map("user" -> List(Track("a", "b"), Track("c", "d"))))
    val interp = playlistInterp :&: videosInterp :&: musicInterp

    val result = FreekAppOps.createPlaylistFromFavoriteTracks("user").interpret(interp)
    println(result)
    val videos = new CatsPlaylistOps[PlaylistDsl].getVideos(result).foldMap(playlistInterp)
    println(videos)
    videos shouldEqual Right(List("a", "1"))
  }
}
