package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 5/01/17.
  */
sealed trait VideoDsl[A]

case class LiteralSearch(literal: SearchLiteral) extends VideoDsl[VideoSearchResult]

