package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 5/01/17.
  */
sealed trait PlaylistDsl[A]

case object CreatePlaylist extends PlaylistDsl[Playlist]
case class GetPlaylistByName(name: String) extends PlaylistDsl[Playlist]
case class GetPlaylistByUrl(url: String) extends PlaylistDsl[Playlist]
case class AddVideo(videoUrl: String, playlist: Playlist) extends PlaylistDsl[Unit]
case class GetVideos(playlist: Playlist) extends PlaylistDsl[List[String]]

