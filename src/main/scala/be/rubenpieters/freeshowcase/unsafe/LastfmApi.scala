package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.{Track, Video}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._

import scalaj.http.{Http, HttpResponse}

/**
  * Created by ruben on 7/01/17.
  */
object LastfmApi {
  val lastfmApiBase = "http://ws.audioscrobbler.com/2.0/?"

  def lastfmUserLovedTracks(user: String) = {
    //method=user.getlovedtracks&user=rj&api_key=YOUR_API_KEY&format=json
    val apiKey = Credentials.getUnsafeProperty("last_fm_api_key")
    val response: HttpResponse[String] =
      Http(s"$lastfmApiBase/search")
        .param("method","user.getlovedtracks")
        .param("user",user)
        .param("api_key",apiKey)
        .param("format", "json")
        .asString
    // TODO: check response.statusLine and return an error if it isn't 200
    println("Response: --")
    println(response.body)
    for {
      decodedResponse <- decode[LastfmLovedTracks](response.body)
    } yield decodedResponse.lovedtracks.track.map(tr =>
      Track(tr.artist.name, tr.name)
    )
  }
}

case class LastfmLovedTracks(lovedtracks: LastfmTrackList)
case class LastfmTrackList(track: List[LastfmTrack])
case class LastfmTrack(name: String, artist: LastfmArtist)
case class LastfmArtist(name: String)