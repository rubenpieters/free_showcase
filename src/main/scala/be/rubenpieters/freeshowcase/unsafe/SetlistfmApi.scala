package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.Track
import be.rubenpieters.freeshowcase.util.UtilImplicits._
import io.circe.optics.JsonPath._
import io.circe.parser._

import scalaj.http.Http

/**
  * Created by ruben on 22/01/17.
  */
object SetlistfmApi {
  val setlistfmApiBase = "https://api.setlist.fm/rest/0.1"

  lazy val setlistfmSearchHttp = Http(s"$setlistfmApiBase/search/setlists.json")

  def setlistfmArtistSearchHttp(artist: String) = setlistfmSearchHttp.param("artistName", artist)

  def setlistfmSetlistForArtist(artist: String): Either[Throwable, List[Track]] = {
    for {
      httpResponse <- setlistfmArtistSearchHttp(artist).doRequest
      parsedHttp <- parseSetlistfmApiJson(httpResponse)
    } yield parsedHttp.map(Track(artist, _))
  }

  /*
   sample json
  {
   "setlists":{
    ...,
    "setlist":[
     {
      ...,
      "sets":{
       "set":{
        "song":[
         ...,
         {
          "@name":"Coma"
         }
        ]
       }
      }
     },
     {
      ...,
      "sets":""
     },
     {
      ...,
      "sets":{
       "set":[
        {
         "song":[
          ...,
          {
           "@name":"Coma"
          }
         ]
        },
        {
         ...,
         "song":{
          "@name":"Melting Sun I: Azure Chimes"
         }
        }
       ]
      }
     }
    ]
   }
  }
  */
  def parseSetlistfmApiJson(json: String): Either[Throwable, List[String]] = {
    for {
      parsedJson <- parse(json)
      setlist <- root.setlists.setlist.each.json.getAllEither(parsedJson, "setlists.setlist")
      sets <- setlist.getObjOrArrAsListEither(root.sets, "setlists.setlist.sets")
      // sometimes `set` is not present in the `sets` object, so we ignore this error silently
      set <- Right(sets
          .getObjOrArrAsListEither(root.set, "setlists.setlist.sets.set")
          .ignoreSilently
      )
      song <- set.getObjOrArrAsListEither(root.song, "setlists.setlist.sets.set.song")
    } yield {
      song.map(s => root.`@name`.string.getEither(s, "setlists.setlist.sets.set.song.@name"))
        .ignoreSilently
        .distinct
    }
  }
}