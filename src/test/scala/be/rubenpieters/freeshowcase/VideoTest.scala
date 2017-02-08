package be.rubenpieters.freeshowcase

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by ruben on 13/01/17.
  */
class VideoTest extends FlatSpec with Matchers {
  "containsTermsFully" should "return true when the title contains all the terms fully" in {
    val title = "abc def ghi"

    Video.containsTermsFully(title, List("abc def", "ghi")) shouldBe true
  }

  "containsTermsFully" should "return false when a term isn't found fully" in {
    val title = "abc def ghi"

    Video.containsTermsFully(title, List("ab def", "ghi")) shouldBe false
  }
}
