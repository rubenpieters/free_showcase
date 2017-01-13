package be.rubenpieters.freeshowcase.freek

import be.rubenpieters.freeshowcase.{Playlist, PlaylistDsl, Track, Video}
import be.rubenpieters.freeshowcase.catsfree._
import org.scalatest.{FlatSpec, Matchers}
import _root_.freek._

import scala.collection.mutable

/**
  * Created by ruben on 8/01/17.
  */
class FreekAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromFavoriteTracks" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val musicInterp = new TestCatsMusicInterp(Map("user" -> List(Track("a", "b"), Track("c", "d"))))
    val interp = playlistInterp :&: videosInterp :&: musicInterp

    val result = FreekAppOps.createPlaylistFromFavoriteTracks("user").interpret(interp)

    playlists(result.url)._2 shouldEqual List("a", "1")
  }

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video("t", i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val interp = playlistInterp :&: videosInterp

    val result = FreekAppOps.createPlaylistFromLiteralList(List("a - b", "c - d")).interpret(interp)

    playlists(result.url)._2 shouldEqual List("a", "1")
  }
}
