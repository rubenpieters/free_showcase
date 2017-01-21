package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.Video
import io.circe.generic.auto._
import io.circe.parser._

import scalaj.http.{Http, HttpResponse}

/**
  * Created by ruben on 5/01/2017.
  */
object YoutubeApi {
  val youtubeApiBase = "https://www.googleapis.com/youtube/v3"

  val youtubeVideoBase = "https://www.youtube.com/watch?v="
  val youtubeLinkPlaylistBase = "https://www.youtube.com/watch_videos?video_ids="

  def youtubeLiteralSearch(literal: String): Either[Throwable, List[Video]] = {
    val apiKey = Credentials.getUnsafeProperty("youtube_api_key")
    val response: HttpResponse[String] =
      Http(s"$youtubeApiBase/search")
        .param("part","snippet")
        .param("q",literal)
        .param("maxResults","10")
        .param("key", apiKey)
        .asString
    // TODO: check response.statusLine and return an error if it isn't 200
//    println("Response: --")
//    println(response.body)
    for {
      decodedResponse <- decode[YoutubeApiResponse[YoutubeSearchResult]](response.body)
    } yield decodedResponse.items.flatMap(item =>
      item.id.videoId.map(i => Video(item.snippet.title, s"$youtubeVideoBase$i", i))
    )
  }
}

case class YoutubeApiResponse[A](items: List[A])

case class YoutubeSearchResult(snippet: YoutubeSearchResultSnippet, id: YoutubeSearchResultId)

case class YoutubeSearchResultId(videoId: Option[String])
case class YoutubeSearchResultSnippet(title: String)