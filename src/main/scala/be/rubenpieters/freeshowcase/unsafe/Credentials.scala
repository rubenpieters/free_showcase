package be.rubenpieters.freeshowcase.unsafe

import java.util.Properties

import cats.implicits._
import be.rubenpieters.freeshowcase.util.SafeWrap._

/**
  * Created by ruben on 5/01/2017.
  */
object Credentials {
  def getProperty(property: String): Either[Throwable, String] = {
    safeWithDefault[Either[Throwable, ?]](getUnsafeProperty(property))
  }

  def getUnsafeProperty(property: String): String = {
    val properties = new Properties()
    val credentialsIS = getClass.getClassLoader.getResourceAsStream("credentials")
    properties.load(credentialsIS)
    properties.getProperty(property)
  }
}