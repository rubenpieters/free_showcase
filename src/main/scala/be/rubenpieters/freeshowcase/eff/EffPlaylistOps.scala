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
  def getPlaylistById[R : _playlist](id: String) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, Playlist]](GetPlaylistById(id))
  def addVideo[R : _playlist](video: Video, playlist: Playlist) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, Unit]](AddVideo(video, playlist))
  def getVideos[R : _playlist](playlist: Playlist) = Eff.send[PlaylistDsl, R, Either[PlaylistDslError, List[String]]](GetVideos(playlist))


  def testRunPlaylist[R, A](currentPlaylists: mutable.Map[String, (Playlist, List[String])])(effects: Eff[R, A])(implicit m: PlaylistDsl <= R): Eff[m.Out, A] = {
    val sideEffect = new SideEffect[PlaylistDsl] {
      override def apply[X](tx: PlaylistDsl[X]): X = tx match {
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

      override def applicative[X, Tr[_] : Traverse](ms: Tr[PlaylistDsl[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
