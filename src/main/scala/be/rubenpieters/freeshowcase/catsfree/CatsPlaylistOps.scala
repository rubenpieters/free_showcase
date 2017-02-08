package be.rubenpieters.freeshowcase.catsfree

import java.util.UUID

import be.rubenpieters.freeshowcase.{CreatePlaylist, _}
import cats.free.{Free, Inject}
import cats.{Id, ~>}
import cats._
import cats.implicits._
import scala.collection.mutable

/**
  * Created by ruben on 5/01/17.
  */
class CatsPlaylistOps[F[_]](implicit I: Inject[PlaylistDsl, F]) {
  def createPlaylist() = Free.inject[PlaylistDsl, F](CreatePlaylist)
  def getPlaylistById(id: String) = Free.inject[PlaylistDsl, F](GetPlaylistById(id))
  def addVideo(video: Video, playlist: Playlist) = Free.inject[PlaylistDsl, F](AddVideo(video, playlist))
  def getVideos(playlist: Playlist) = Free.inject[PlaylistDsl, F](GetVideos(playlist))
}

object CatsPlaylistOps {
  implicit def videoOps[F[_]](implicit I: Inject[PlaylistDsl, F]): CatsPlaylistOps[F] = new CatsPlaylistOps[F]
}

class TestCatsPlaylistInterp(currentPlaylists: mutable.Map[String, (Playlist, List[String])]) extends (PlaylistDsl ~> Id) {

  override def apply[A](fa: PlaylistDsl[A]): Id[A] = fa match {
    case CreatePlaylist =>
      val simulatedId = UUID.randomUUID().toString
      val playlist = Playlist(simulatedId)
      currentPlaylists.update(simulatedId, (playlist, List()))
      playlist
    case GetPlaylistById(id) =>
      Either.fromOption(currentPlaylists.get(id).map(_._1), new PlaylistNotFound)
    case AddVideo(video, playlist) =>
      Either.fromOption(for {
        foundPlaylistWithVideos <- currentPlaylists.get(playlist.id)
        (foundPlaylist, foundVideos) = foundPlaylistWithVideos
        _ = currentPlaylists.update(foundPlaylist.id, (foundPlaylist, foundVideos :+ video.url))
      } yield ()
        , new PlaylistNotFound)
    case GetVideos(playlist) =>
      Either.fromOption(currentPlaylists.get(playlist.id).map(_._2), new PlaylistNotFound)
  }
}
