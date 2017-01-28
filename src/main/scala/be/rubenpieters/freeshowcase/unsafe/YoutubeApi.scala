package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.Video
import be.rubenpieters.freeshowcase.util.UtilImplicits._
import be.rubenpieters.freeshowcase.util.json.JsonReadError
import cats.data.Validated
import io.circe.Json
import io.circe.optics.JsonPath._
import io.circe.parser._
import cats.implicits._

import scalaj.http.{Http, HttpRequest}

/**
  * Created by ruben on 5/01/2017.
  */
object YoutubeApi {
  val youtubeApiBase = "https://www.googleapis.com/youtube/v3"
  val youtubeVideoBase = "https://www.youtube.com/watch?v="
  val youtubeLinkPlaylistBase = "https://www.youtube.com/watch_videos?video_ids="

  val apiKey = Credentials.getUnsafeProperty("youtube_api_key")

  val youtubeSearchHttp = Http(s"$youtubeApiBase/search")
    .param("part", "snippet")
    .param("maxResults", "10")
    .param("key", apiKey)

  def youtubeLiteralSearchHttp(literal: String): HttpRequest =
    youtubeSearchHttp.param("q", literal)

  def youtubeLiteralSearch(literal: String): Either[Throwable, List[Video]] =
    youtubeLiteralSearchHttp(literal)
      .doRequest
      .flatMap(parseYoutubeApiJson)

  /*
  json sample:
  {
    ...,
    "items": [
    ...,
    {
      ...,
      "id": {
        ...,
        "videoId": "U0lK0hnbc4U"
      },
      "snippet": {
        ...,
        "title": "Why the free Monad isn't free - by Kelley Robinson"
      }
    },
    ...
    ]
  }
  */
  val items = root.items.each
  def parseYoutubeApiJson(json: String): Either[Throwable, List[Video]] =
    for {
      parsedJson <- parse(json)
      parsedItems <- items.json.getNonEmptyEither(parsedJson, "items")
    } yield {
      // TODO: currently we ignore errors silently, I want to model this differently so we can aggregate read errors
      // (possibly with Validated)
      val parsedVideoFields = parsedItems.map(getVideoTuple).ignoreSilently
      parsedVideoFields.map(videoFromTitleAndYoutubeIdTupled)
    }

  val snippetTitle = root.snippet.title.string
  val idVideoId = root.id.videoId.string
  def getVideoTuple(itemJson: Json): Either[JsonReadError, (String, String)] = for {
    title <- snippetTitle.getEither(itemJson, "items.snippet.title")
    videoId <- idVideoId.getEither(itemJson, "items.id.videoId")
  } yield (title, videoId)

  def videoFromTitleAndYoutubeId(title: String, id: String): Video =
    Video(title, s"$youtubeVideoBase$id", id)

  val videoFromTitleAndYoutubeIdTupled = (videoFromTitleAndYoutubeId(_, _)).tupled
}