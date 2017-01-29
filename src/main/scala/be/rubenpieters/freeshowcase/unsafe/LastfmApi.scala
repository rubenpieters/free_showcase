package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.Track
import be.rubenpieters.freeshowcase.util.UtilImplicits._
import be.rubenpieters.freeshowcase.util.json.JsonReadError
import io.circe._
import io.circe.optics.JsonPath._
import io.circe.parser._

import scalaj.http.Http

/**
  * Created by ruben on 7/01/17.
  */
object LastfmApi {
  val lastfmApiBase = "http://ws.audioscrobbler.com/2.0/?"

  lazy val apiKey = Credentials.getUnsafeProperty("last_fm_api_key")

  lazy val lastfmSearchHttp = Http(s"$lastfmApiBase/search")
    .param("api_key",apiKey)
    .param("format", "json")

  def lastfmUserLovedTracksHttp(user: String) = lastfmSearchHttp
    .param("method", "user.getlovedtracks")
    .param("user", user)

  def lastfmUserLovedTracks(user: String) =
    lastfmUserLovedTracksHttp(user)
      .doRequest
      .flatMap(parseLastfmApiJson)
  /*
  json sample:
  {
    "lovedtracks":{
      "track":[
        {
          "name":"Pulse/Surreal",
          ...,
          "artist":{
            "name":"Lantlôs",
            ...
          },
          ...
        },
        ...
      ],
      ...
    }
  }
  */
  val tracks = root.lovedtracks.track.each
  def parseLastfmApiJson(json: String) =
    for {
      parsedJson <- parse(json)
      parsedTracks <- tracks.json.getAllEither(parsedJson, "lovedtracks.track")
    } yield {
      val parsedTrackTuples = parsedTracks.map(getTrackTuple).ignoreSilently
      parsedTrackTuples.map{ case (artist, track) => Track(artist, track) }
    }

  def getTrackTuple(itemJson: Json): Either[JsonReadError, (String, String)] =
    for {
      trackName <- root.name.string.getEither(itemJson, "lovedtracks.track.name")
      artistName <- root.artist.name.string.getEither(itemJson, "lovedtracks.track.artist.name")
    } yield (artistName, trackName)
}