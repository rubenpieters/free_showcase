package be.rubenpieters.freeshowcase.freek

import _root_.freek._
import be.rubenpieters.freeshowcase.catsfree._
import be.rubenpieters.freeshowcase.{Playlist, Track, Video}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by ruben on 8/01/17.
  */
class FreekAppOpsTest extends FlatSpec with Matchers {

  "createPlaylistFromSearchableList" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a - b", "b", "c"), "c - d" -> List("c - d", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val interp = playlistInterp :&: videosInterp

    val result = FreekAppOps.createPlaylistFromSearchableList(List(Track("a", "b"), Track("c", "d"))).interpret(interp)

    playlists(result.id)._2 shouldEqual List("a - b", "c - d")
  }

  "createPlaylistFromFavoriteTracks" should "correctly create a playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a - b", "b", "c"), "c - d" -> List("c - d", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val musicInterp = new TestCatsMusicInterp(Map("user" -> List(Track("a", "b"), Track("c", "d"))))
    val interp = playlistInterp :&: videosInterp :&: musicInterp

    val result = FreekAppOps.createPlaylistFromFavoriteTracks("user").interpret(interp)

    playlists(result.id)._2 shouldEqual List("a - b", "c - d")
  }

  "createPlaylistFromArtistSetlist" should "correctly create the playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("artist - song1" -> List("artist - song1", "b", "c"), "artist - song2" -> List("artist - song2", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val setlistInterp = new TestCatsSetlistInterp(Map("artist" -> List(Track("artist", "song1"), Track("artist", "song2"))))
    val interp = playlistInterp :&: videosInterp :&: setlistInterp

    val result = FreekAppOps.createPlaylistFromArtistSetlist("artist").interpret(interp)

    playlists(result.id)._2 shouldEqual List("artist - song1", "artist - song2")
  }
}
