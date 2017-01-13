package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 5/01/17.
  */
sealed trait PlaylistDsl[A]

case object CreatePlaylist extends PlaylistDsl[Playlist]
case class GetPlaylistById(id: String) extends PlaylistDsl[Either[PlaylistDslError, Playlist]]
case class AddVideo(video: Video, playlist: Playlist) extends PlaylistDsl[Either[PlaylistDslError, Unit]]
case class GetVideos(playlist: Playlist) extends PlaylistDsl[Either[PlaylistDslError, List[String]]]

sealed trait PlaylistDslError extends Exception
class PlaylistNotFound extends PlaylistDslError