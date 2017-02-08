package be.rubenpieters.freeshowcase

import simulacrum.typeclass

/**
  * Created by ruben on 8/02/17.
  */
@typeclass trait Searchable[A] {
  def asSearchLiteral(a: A): SearchLiteral
  def asSearchTerms(a: A): SearchTerms
}

object Searchable {
  implicit val trackSearchable: Searchable[Track] = new Searchable[Track] {
    override def asSearchLiteral(a: Track): SearchLiteral = asSearchTerms(a).mkString(" - ")
    override def asSearchTerms(a: Track): SearchTerms = List(a.artist, a.title)
  }
}
