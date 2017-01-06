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
    case GetPlaylistByName(name) =>
      leftIfNotExists(name, Playlist(name))
    case GetPlaylistByUrl(url) =>
      leftIfNotExists(url, Playlist(url))
    case AddVideo(video, playlist) =>
      leftIfNotExists(playlist.url, playlistMap.update(playlist.url, playlistMap.getOrElse(playlist.url, List()) :+ video.videoId))
    case GetVideos(playlist) =>
      leftIfNotExists(playlist.url, playlistMap(playlist.url))
  }

  def leftIfNotExists[A](name: String, a: A): Either[PlaylistDslError, A] = playlistMap.contains(name) match {
    case true => Right(a)
    case false => Left(new PlaylistNotFound)
  }
}
