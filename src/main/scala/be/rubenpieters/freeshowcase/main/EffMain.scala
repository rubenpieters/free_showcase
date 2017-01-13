package be.rubenpieters.freeshowcase.main

import be.rubenpieters.freeshowcase.{MusicDsl, PlaylistDsl, VideoDsl}
import be.rubenpieters.freeshowcase.eff.{EffAppOps, EffMusicLastfmInterpreter, EffPlaylistLinkInterpreter, EffVideoYoutubeInterpreter}
import org.atnos.eff.Fx
import org.atnos.eff._
import syntax.all._

/**
  * Created by ruben on 13/01/17.
  */
object EffMain {
  def main(args: Array[String]) = {
    val result = EffMusicLastfmInterpreter.runMusic(
      EffVideoYoutubeInterpreter.runVideo(
        EffPlaylistLinkInterpreter.runPlaylist(
          EffAppOps.createPlaylistFromFavoriteTracks[Fx.fx4[PlaylistDsl, VideoDsl, MusicDsl, Either[Throwable, ?]]]("rubenpieters"))))
      .runEither.run

    println(result)
  }
}
