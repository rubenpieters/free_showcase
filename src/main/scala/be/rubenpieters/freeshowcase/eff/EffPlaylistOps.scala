package be.rubenpieters.freeshowcase.eff

import java.util.UUID

import be.rubenpieters.freeshowcase.{AddVideo, GetVideos, Playlist, Video, _}
import cats.Traverse
import cats.implicits._
import org.atnos.eff.MemberIn._
import org.atnos.eff.interpret._
import org.atnos.eff.{SideEffect, |= => _, _}

import scala.collection.mutable

/**
  * Created by ruben on 6/01/2017.
  */
object EffPlaylistOps {
  type _playlist[R] = PlaylistDsl |= R

  def createPlaylist[R : _playlist]() = Eff.send[PlaylistDsl, R, Playlist](CreatePlaylist)
  def getPlaylistByName[R : _playlist](name: String) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, Playlist]](GetPlaylistByName(name))
  def getPlaylistByUrl[R : _playlist](url: String) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, Playlist]](GetPlaylistByUrl(url))
  def addVideo[R : _playlist](video: Video, playlist: Playlist) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, Unit]](AddVideo(video, playlist))
  def getVideos[R : _playlist](playlist: Playlist) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, List[String]]](GetVideos(playlist))


  def testRunPlaylist[R, A](currentPlaylists: mutable.Map[String, (Playlist, List[String])])(effects: Eff[R, A])(implicit m: PlaylistDsl <= R): Eff[m.Out, A] = {
    val sideEffect = new SideEffect[PlaylistDsl] {
      override def apply[X](tx: PlaylistDsl[X]): X = tx match {
        case CreatePlaylist =>
          val simulatedUrl = UUID.randomUUID().toString
          val playlist = Playlist(simulatedUrl)
          currentPlaylists.update(simulatedUrl, (playlist, List()))
          playlist
        case GetPlaylistByName(name) =>
          Either.fromOption(currentPlaylists.get(name).map(_._1), new PlaylistNotFound)
        case GetPlaylistByUrl(url) =>
          Either.fromOption(currentPlaylists.get(url).map(_._1), new PlaylistNotFound)
        case AddVideo(video, playlist) =>
          Either.fromOption(for {
            foundPlaylistWithVideos <- currentPlaylists.get(playlist.url)
            (foundPlaylist, foundVideos) = foundPlaylistWithVideos
            _ = currentPlaylists.update(foundPlaylist.url, (foundPlaylist, foundVideos :+ video.url))
          } yield ()
            , new PlaylistNotFound)
        case GetVideos(playlist) =>
          Either.fromOption(currentPlaylists.get(playlist.url).map(_._2), new PlaylistNotFound)
      }

      override def applicative[X, Tr[_] : Traverse](ms: Tr[PlaylistDsl[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
