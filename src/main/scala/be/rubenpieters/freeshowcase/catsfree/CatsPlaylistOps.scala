package be.rubenpieters.freeshowcase.catsfree

import java.util.UUID

import be.rubenpieters.freeshowcase.{CreatePlaylist, _}
import cats.free.{Free, Inject}
import cats.{Id, ~>}

/**
  * Created by ruben on 5/01/17.
  */
class CatsPlaylistOps[F[_]](implicit I: Inject[PlaylistDsl, F]) {
  def createPlaylist() = Free.inject[PlaylistDsl, F](CreatePlaylist)
  def getPlaylistByName(name: String) = Free.inject[PlaylistDsl, F](GetPlaylistByName(name))
  def getPlaylistByUrl(url: String) = Free.inject[PlaylistDsl, F](GetPlaylistByUrl(url))
  def addVideo(videoUrl: String, playlist: Playlist) = Free.inject[PlaylistDsl, F](AddVideo(videoUrl, playlist))
  def getVideos(playlist: Playlist) = Free.inject[PlaylistDsl, F](GetVideos(playlist))
}

object CatsPlaylistOps {
  implicit def videoOps[F[_]](implicit I: Inject[PlaylistDsl, F]): CatsPlaylistOps[F] = new CatsPlaylistOps[F]
}

class TestCatsPlaylistInterp extends (PlaylistDsl ~> Id) {
  import scala.collection.mutable

  val currentPlaylists: mutable.Map[String, (Playlist, List[String])] = mutable.Map()

  override def apply[A](fa: PlaylistDsl[A]): Id[A] = fa match {
    case CreatePlaylist =>
      val simulatedUrl = UUID.randomUUID().toString
      val playlist = Playlist(simulatedUrl)
      currentPlaylists.update(simulatedUrl, (playlist, List()))
      playlist
    case GetPlaylistByName(name) =>
      currentPlaylists(name)._1
    case GetPlaylistByUrl(url) =>
      currentPlaylists(url)._1
    case AddVideo(videoUrl, playlist) =>
      (for {
        foundPlaylistWithVideos <- currentPlaylists.get(playlist.url)
        (foundPlaylist, foundVideos) = foundPlaylistWithVideos
        _ = currentPlaylists.update(foundPlaylist.url, (foundPlaylist, foundVideos :+ videoUrl))
      } yield ()
        ).getOrElse(())
    case GetVideos(playlist) =>
      currentPlaylists(playlist.url)._2
  }
}