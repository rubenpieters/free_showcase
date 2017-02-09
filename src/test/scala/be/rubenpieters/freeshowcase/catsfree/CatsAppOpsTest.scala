package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.{Playlist, PlaylistDsl, Track, Video}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by ruben on 5/01/17.
  */
class CatsAppOpsTest extends FlatSpec with Matchers {
  "createPlaylistFromSearchableList" should "create the playlist from literal terms" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a - b", "b", "c"), "c - d" -> List("c - d", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val interp = CatsAppOps.mkPlaylistVideoInterp(playlistInterp, videosInterp)

    val result = CatsAppOps.createPlaylistFromSearchableList[CatsAppOps.PlaylistVideoApp, Track](List(Track("a", "b"), Track("c", "d"))).foldMap(interp)

    playlists(result.id)._2 shouldEqual List("a - b", "c - d")
  }

  "createPlaylistFromFavoriteTracks" should "correctly create the playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a - b", "b", "c"), "c - d" -> List("c - d", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val musicInterp = new TestCatsMusicInterp(Map("user" -> List(Track("a", "b"), Track("c", "d"))))
    val interp = CatsAppOps.mkMusicPlaylistVideoInterp(playlistInterp, videosInterp, musicInterp)

    val result = CatsAppOps.createPlaylistFromFavoriteTracks[CatsAppOps.MusicPlaylistVideoApp]("user").foldMap(interp)

    playlists(result.id)._2 shouldEqual List("a - b", "c - d")
  }

  "createPlaylistFromFavoriteTracks" should "ignore when a result is not relevant" in {
    val videosInterp = new TestCatsVideoInterp(Map("a - b" -> List("a", "b", "c"), "c - d" -> List("1", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val musicInterp = new TestCatsMusicInterp(Map("user" -> List(Track("a", "b"), Track("c", "d"))))
    val interp = CatsAppOps.mkMusicPlaylistVideoInterp(playlistInterp, videosInterp, musicInterp)

    val result = CatsAppOps.createPlaylistFromFavoriteTracks[CatsAppOps.MusicPlaylistVideoApp]("user").foldMap(interp)

    playlists(result.id)._2 shouldEqual List()
  }

  "createPlaylistFromArtistSetlist" should "correctly create the playlist" in {
    val videosInterp = new TestCatsVideoInterp(Map("artist - song1" -> List("artist - song1", "b", "c"), "artist - song2" -> List("artist - song2", "2", "3")).mapValues(_.map(i => Video(i, i, i))))
    val playlists = mutable.Map[String, (Playlist, List[String])]()
    val playlistInterp = new TestCatsPlaylistInterp(playlists)
    val musicInterp = new TestCatsMusicInterp(Map())
    val setlistInterp = new TestCatsSetlistInterp(Map("artist" -> List(Track("artist", "song1"), Track("artist", "song2"))))
    val interp = CatsAppOps.mkSetlistPlaylistVideoInterp(playlistInterp, videosInterp, setlistInterp)

    val result = CatsAppOps.createPlaylistFromArtistSetlist[CatsAppOps.SetlistPlaylistVideoApp]("artist").foldMap(interp)

    playlists(result.id)._2 shouldEqual List("artist - song1", "artist - song2")
  }

}
