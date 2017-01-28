package be.rubenpieters.freeshowcase

import be.rubenpieters.freeshowcase.util.UtilImplicits
import org.scalatest.{FlatSpec, Inside, Matchers}

import scala.io.Source

/**
  * Created by ruben on 28/01/17.
  */
trait TestSpec
  extends FlatSpec
    with Matchers
    with Inside
    with UtilImplicits {
  def testIterator(fileName: String): Iterator[String] =
    Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(fileName)).getLines()

  def testString(fileName: String): String =
    testIterator(fileName).mkString("")
}
