package be.rubenpieters.freeshowcase

import cats.free.Inject

/**
  * Created by ruben on 5/01/17.
  */
sealed trait VideoDsl[A]

case class LiteralSearch(literal: SearchLiteral) extends VideoDsl[VideoSearchResult]

