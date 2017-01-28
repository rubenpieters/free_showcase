package be.rubenpieters.freeshowcase.util.http

import scalaj.http.HttpRequest

/**
  * Created by ruben on 28/01/17.
  */
object HttpUtils {
  trait implicits {
    implicit class EnrichedHttpRequest(httpRequest: HttpRequest) {
      def doRequest: Either[Throwable, String] = {
        val response = httpRequest.asString
        response.is2xx match {
          case true => Right(response.body)
          case false => Left(HttpUtils.HttpUnexpectedResponseCode(response.code))
        }
      }
    }
  }

  final case class HttpUnexpectedResponseCode(code: Int) extends Exception(s"unexpected HTTP response code: $code")
}
