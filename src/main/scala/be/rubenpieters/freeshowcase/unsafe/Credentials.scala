package be.rubenpieters.freeshowcase.unsafe

import java.util.Properties
import cats.implicits._

/**
  * Created by ruben on 5/01/2017.
  */
object Credentials {
  def getProperty(property: String): Either[Throwable, String] = {
    Either.catchNonFatal(getUnsafeProperty(property))
  }

  def getUnsafeProperty(property: String): String = {
    val properties = new Properties()
    val credentialsIS = getClass.getClassLoader.getResourceAsStream("credentials")
    properties.load(credentialsIS)
    properties.getProperty(property)
  }
}
