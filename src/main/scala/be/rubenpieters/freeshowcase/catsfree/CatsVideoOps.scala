package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase._
import cats._
import cats.free.{Free, Inject}
import be.rubenpieters.freeshowcase.Searchable.ops._

/**
  * Created by ruben on 5/01/17.
  */

class CatsVideoOps[F[_]](implicit I: Inject[VideoDsl, F]) {
  def literalSearch(literal: String) = Free.inject[VideoDsl, F](LiteralSearch(literal))

  def literalSearchableSearch[A: Searchable](a: A) = literalSearch(a.asSearchLiteral)
}

object CatsVideoOps {
  implicit def videoOps[F[_]](implicit I: Inject[VideoDsl, F]): CatsVideoOps[F] = new CatsVideoOps[F]
}

class TestCatsVideoInterp(results: Map[String, List[Video]]) extends (VideoDsl ~> Id) {

  override def apply[A](fa: VideoDsl[A]): Id[A] = fa match {
    case LiteralSearch(literal) =>
      VideoSearchResult(results.getOrElse(literal, List()))
  }
}