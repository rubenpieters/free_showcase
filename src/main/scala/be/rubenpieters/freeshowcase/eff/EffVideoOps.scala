package be.rubenpieters.freeshowcase.eff

import be.rubenpieters.freeshowcase._
import cats.Traverse
import org.atnos.eff.Eff
import org.atnos.eff.MemberIn.|=
import org.atnos.eff._
import interpret._
import cats.implicits._
import cats.data._
import be.rubenpieters.freeshowcase.Searchable.ops._

/**
  * Created by ruben on 6/01/2017.
  */
object EffVideoOps {
  type _video[R] = VideoDsl |= R

  def literalSearch[R : _video](literal: SearchLiteral): Eff[R, VideoSearchResult] =
    Eff.send[VideoDsl, R, VideoSearchResult](LiteralSearch(literal))

  def literalSearchableSearch[R : _video, A: Searchable](a: A): Eff[R, VideoSearchResult] =
    literalSearch(a.asSearchLiteral)

  def testRunVideo[R, A](results: Map[String, List[Video]])(effects: Eff[R, A])(implicit m: VideoDsl <= R): Eff[m.Out, A] = {
    val sideEffect = new SideEffect[VideoDsl] {
      override def apply[X](tx: VideoDsl[X]): X = tx match {
        case LiteralSearch(literal) =>
          VideoSearchResult(results.getOrElse(literal, List()))
      }

      override def applicative[X, Tr[_] : Traverse](ms: Tr[VideoDsl[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
