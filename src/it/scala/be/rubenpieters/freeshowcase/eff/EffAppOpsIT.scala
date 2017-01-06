package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase.{PlaylistDsl, Video, VideoDsl}
import org.atnos.eff.Fx
import org.scalatest.{FlatSpec, Matchers}
import org.atnos.eff._
import syntax.all._


/**
  * Created by ruben on 6/01/2017.
  */
class EffAppOpsIT extends FlatSpec with Matchers {

  "createPlaylistFromLiteralList" should "correctly create a playlist" in {
    val result = EffVideoYoutubeInterpreter.runVideo(
      EffPlaylistLinkInterpreter.runPlaylist(
        EffAppOps.createPlaylistFromLiteralList[Fx.fx3[PlaylistDsl, VideoDsl, Either[Throwable, ?]]](List("free monad", "free applicative"))))
      .runEither.run

    println(result)

//    val videos = EffPlaylistOps.testRunPlaylist(EffPlaylistOps.getVideos[Fx.fx1[PlaylistDsl]](result)).run
//    println(videos)
    // doesn't work because we cannot reuse the interpreter state
    //    videos shouldEqual Right(List("a", "1"))
  }
}
