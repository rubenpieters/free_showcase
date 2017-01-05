package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.VideoDsl
import org.scalatest.{FlatSpec, Matchers}
import cats._
import cats.implicits._

/**
  * Created by ruben on 5/01/2017.
  */
class CatsVideoYoutubeInterpreterIT extends FlatSpec with Matchers {
  "cats youtube interpreter" should "search videos" in {
    val result = new CatsVideoOps[VideoDsl].literalSearch("free monad").foldMap(new CatsVideoYoutubeInterpreter)
    println(result)
  }
}
