package be.rubenpieters.freeshowcase.freek

import be.rubenpieters.freeshowcase.{LiteralSearch, Searchable}
import be.rubenpieters.freeshowcase.Searchable.ops._

/**
  * Created by ruben on 9/02/2017.
  */
object FreekVideoOps {
  def literalSearchableSearch[A: Searchable](a: A) = LiteralSearch(a.asSearchLiteral)
}
