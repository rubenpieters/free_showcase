package be.rubenpieters.freeshowcase.eff

import java.util.UUID

import be.rubenpieters.freeshowcase.unsafe.YoutubeApi
import be.rubenpieters.freeshowcase.{AddVideo, GetPlaylistByUrl, GetVideos, _}
import cats.Traverse
import org.atnos.eff.interpret._
import org.atnos.eff.{SideEffect, _}

import scala.collection.mutable
import cats.implicits._

/**
  * Created by ruben on 6/01/2017.
  */
object EffPlaylistLinkInterpreter {
  def runPlaylist[R, A](effects: Eff[R, A])(implicit m: PlaylistDsl <= R): Eff[m.Out, A] = {
    val playlistMap: mutable.Map[String, List[String]] = mutable.Map()

    def playlistByName(name: String) = s"${YoutubeApi.youtubeLinkPlaylistBase}${playlistMap(name).mkString(",")}"

    val sideEffect = new SideEffect[PlaylistDsl] {
      override def apply[X](tx: PlaylistDsl[X]): X = tx match {
        case CreatePlaylist =>
          Playlist(UUID.randomUUID().toString)
        case GetPlaylistByName(name) =>
          leftIfNotExists(name, Playlist(name))
        case GetPlaylistByUrl(url) =>
          leftIfNotExists(url, Playlist(url))
        case AddVideo(video, playlist) =>
          val result = leftIfNotExists(playlist.url, playlistMap.update(playlist.url, playlistMap.getOrElse(playlist.url, List()) :+ video.videoId))
          println("current playlist link: " + playlistByName(playlist.url))
          result
        case GetVideos(playlist) =>
          leftIfNotExists(playlist.url, playlistMap(playlist.url))
      }

      def leftIfNotExists[A](name: String, a: A): Either[PlaylistDslError, A] = playlistMap.contains(name) match {
        case true => Right(a)
        case false => Left(new PlaylistNotFound)
      }

      override def applicative[X, Tr[_] : Traverse](ms: Tr[PlaylistDsl[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
