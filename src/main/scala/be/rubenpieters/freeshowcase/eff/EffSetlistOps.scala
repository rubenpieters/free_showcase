package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase.{ArtistName, GetSetlistTracksForArtist, SetlistDsl, Track}
import cats.Traverse
import org.atnos.eff.Eff
import org.atnos.eff._
import interpret._
import cats.implicits._

/**
  * Created by ruben on 9/02/2017.
  */
object EffSetlistOps {
  type _setlist[R] = SetlistDsl |= R

  def getSetlistTracksForArtist[R : _setlist](artist: ArtistName): Eff[R, List[Track]] =
    Eff.send[SetlistDsl, R, List[Track]](GetSetlistTracksForArtist(artist))

  def testRunSetlist[R, A](tracks: Map[ArtistName, List[Track]])(effects: Eff[R, A])(implicit m: SetlistDsl <= R): Eff[m.Out, A] = {
    val sideEffect = new SideEffect[SetlistDsl] {
      override def apply[X](tx: SetlistDsl[X]): X = tx match {
        case GetSetlistTracksForArtist(artist) =>
          tracks(artist)
      }

      override def applicative[X, Tr[_] : Traverse](ms: Tr[SetlistDsl[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
