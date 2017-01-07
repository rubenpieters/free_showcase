package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase._
import cats.Traverse
import org.atnos.eff.Eff
import org.atnos.eff.MemberIn.|=
import org.atnos.eff._
import interpret._
import cats.implicits._

/**
  * Created by ruben on 7/01/17.
  */
object EffMusicOps {
  type _music[R] = MusicDsl |= R

  def favoriteTracksForUser[R: _music](user: String): Eff[R, List[Track]] =
    Eff.send[MusicDsl, R, List[Track]](FavoriteTracksForUser(user))

  def testRunMusic[R, A](tracks: Map[String, List[Track]])(effects: Eff[R, A])(implicit m: MusicDsl <= R): Eff[m.Out, A] = {
    val sideEffect = new SideEffect[MusicDsl] {
      override def apply[X](tx: MusicDsl[X]): X = tx match {
        case FavoriteTracksForUser(user) =>
          tracks.getOrElse(user, List())
      }

      override def applicative[X, Tr[_] : Traverse](ms: Tr[MusicDsl[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
