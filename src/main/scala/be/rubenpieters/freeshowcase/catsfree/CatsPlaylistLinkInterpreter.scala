package be.rubenpieters.freeshowcase.catsfree

import java.util.UUID

import be.rubenpieters.freeshowcase._
import be.rubenpieters.freeshowcase.unsafe.YoutubeApi
import cats.{Id, ~>}

import scala.collection.mutable

/**
  * Created by ruben on 6/01/2017.
  */
class CatsPlaylistLinkInterpreter extends (PlaylistDsl ~> Id) {
  val playlistMap: mutable.Map[String, List[String]] = mutable.Map()

  def playlistByName(name: String) = s"${YoutubeApi.youtubeLinkPlaylistBase}${playlistMap(name).mkString(",")}"

  override def apply[A](fa: PlaylistDsl[A]): Id[A] = fa match {
    case CreatePlaylist =>
      Playlist(UUID.randomUUID().toString)
    case GetPlaylistById(id) =>
      leftIfNotExists(id, Playlist(id))
    case AddVideo(video, playlist) =>
      leftIfNotExists(playlist.id, playlistMap.update(playlist.id, playlistMap.getOrElse(playlist.id, List()) :+ video.videoId))
    case GetVideos(playlist) =>
      leftIfNotExists(playlist.id, playlistMap(playlist.id))
  }

  def leftIfNotExists[A](name: String, a: A): Either[PlaylistDslError, A] = playlistMap.contains(name) match {
    case true => Right(a)
    case false => Left(new PlaylistNotFound)
  }
}
