package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase._
import be.rubenpieters.freeshowcase.unsafe.{LastfmApi, YoutubeApi}
import org.atnos.eff.{Eff, Member, Translate, _}
import org.atnos.eff.eff._
import org.atnos.eff.interpret._

/**
  * Created by ruben on 7/01/17.
  */
object EffMusicLastfmInterpreter {
  type MusicError[A] = Either[Throwable, A]
  type _MusicErr[R] = MusicError |= R

  def runMusic[R, U, A](effects: Eff[R, A])(implicit m: Member.Aux[MusicDsl, R, U], ve: _MusicErr[U]): Eff[U, A] = {
    val _translate = new Translate[MusicDsl, U] {
      override def apply[X](kv: MusicDsl[X]): Eff[U, X] = kv match {
        case FavoriteTracksForUser(user) =>
          val result = LastfmApi.lastfmUserLovedTracks(user)
          send[MusicError, U, List[Track]](result)

      }
    }
    translate(effects)(_translate)
  }
}
