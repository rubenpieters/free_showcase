package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.Track
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import cats.syntax.either._

import scalaj.http.{Http, HttpResponse}

/**
  * Created by ruben on 22/01/17.
  */
object SetlistfmApi {
  val setlistfmApiBase = "https://api.setlist.fm/rest/0.1"

  def setlistfmSetlistForArtist(artist: String) = {
    //    val apiKey = Credentials.getUnsafeProperty("setlist_fm_api_key")
    val response: HttpResponse[String] =
    Http(s"$setlistfmApiBase/search/setlists.json")
      .param("artistName",artist)
      .asString
    // TODO: check response.statusLine and return an error if it isn't 200
//    println("Response: --")
//    println(response.body)
    for {
    // TODO: look for a cleaner way to handle "sets":"", output from the setlistfm API
      decodedResponse <- decode[SetlistfmResponse](response.body.replaceAll("\"sets\":\"\",", ""))
    } yield (for {
      sl <- decodedResponse.setlists.setlist
      sets <- sl.sets.toIterable
      set <- sets.set match {
        case Left(a) => List(a)
        case Right(a) => a
      }
      song <- set.song match {
        case Left(a) => List(a)
        case Right(a) => a
      }
    } yield song
      )
      .map(s => Track(artist, s.`@name`))
      .distinct
  }
}

case class SetlistfmResponse(setlists: SetlistfmInnerResponse)
case class SetlistfmInnerResponse(setlist: List[SetlistfmSetlist])
case class SetlistfmSetlist(`@eventDate`: String, artist: SetlistfmArtist, sets: Option[SetlistfmSets])
case class SetlistfmArtist(`@name`: String)
case class SetlistfmSets(set: Either[SetlistfmSet, List[SetlistfmSet]])

object SetlistfmSets {
  implicit val decodeTestA: Decoder[SetlistfmSets] =
    Decoder[SetlistfmSet].map(Left(_)).or(
      Decoder[List[SetlistfmSet]].map(Right(_): Either[SetlistfmSet, List[SetlistfmSet]])
    ).prepare(_.downField("set")).map(SetlistfmSets(_))
}

case class SetlistfmSet(song: Either[SetlistfmSetSong, List[SetlistfmSetSong]])

object SetlistfmSet {
  implicit val decodeTestA: Decoder[SetlistfmSet] =
    Decoder[SetlistfmSetSong].map(Left(_)).or(
      Decoder[List[SetlistfmSetSong]].map(Right(_): Either[SetlistfmSetSong, List[SetlistfmSetSong]])
    ).prepare(_.downField("song")).map(SetlistfmSet(_))
}

case class SetlistfmSetSong(`@name`: String)

object Test extends App {
  val result = decode[SetlistfmResponse](responseBody.replaceAll("\"sets\":\"\",", ""))

  println(responseBody.replaceAll("\"sets\":\"\",", ""))

  println(result)

  def responseBody = """{
                       | "setlists":{
                       |  "@itemsPerPage":"20",
                       |  "@page":"1",
                       |  "@total":"28",
                       |  "setlist":[
                       |   {
                       |    "@eventDate":"01-04-2016",
                       |    "@id":"3bf05c1c",
                       |    "@lastUpdated":"2016-04-11T14:05:30.000+0000",
                       |    "@versionId":"7ba2c6f8",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"3d61d7f",
                       |     "@name":"Stadthalle",
                       |     "city":{
                       |      "@id":"2878074",
                       |      "@name":"Lichtenfels",
                       |      "@state":"Bavaria",
                       |      "@stateCode":"02",
                       |      "coords":{
                       |       "@lat":"50.1456663002208",
                       |       "@long":"11.0592842102051"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/stadthalle-lichtenfels-germany-3d61d7f.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Intrauterin"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2016\/stadthalle-lichtenfels-germany-3bf05c1c.html"
                       |   },
                       |   {
                       |    "@eventDate":"29-01-2016",
                       |    "@id":"43f3cbd7",
                       |    "@lastUpdated":"2016-02-01T09:25:03.000+0000",
                       |    "@versionId":"ba68936",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"43d67b6b",
                       |     "@name":"Theaterfabrik",
                       |     "city":{
                       |      "@id":"2867714",
                       |      "@name":"Munich",
                       |      "@state":"Bavaria",
                       |      "@stateCode":"02",
                       |      "coords":{
                       |       "@lat":"48.1374325498109",
                       |       "@long":"11.5754914283752"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/theaterfabrik-munich-germany-43d67b6b.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2016\/theaterfabrik-munich-germany-43f3cbd7.html"
                       |   },
                       |   {
                       |    "@eventDate":"27-01-2016",
                       |    "@id":"2bf3e04a",
                       |    "@lastUpdated":"2016-01-27T23:08:27.000+0000",
                       |    "@versionId":"6bb94a4e",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"3bd61c64",
                       |     "@name":"SO36",
                       |     "city":{
                       |      "@id":"2950159",
                       |      "@name":"Berlin",
                       |      "@state":"Berlin",
                       |      "@stateCode":"16",
                       |      "coords":{
                       |       "@lat":"52.5166667",
                       |       "@long":"13.4"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/so36-berlin-germany-3bd61c64.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Melting Sun III: Aquamarine Towers"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2016\/so36-berlin-germany-2bf3e04a.html"
                       |   },
                       |   {
                       |    "@eventDate":"16-10-2015",
                       |    "@id":"3bf5f448",
                       |    "@lastUpdated":"2015-10-25T17:14:56.000+0000",
                       |    "@versionId":"5bbc2760",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"73d63225",
                       |     "@name":"Astra",
                       |     "city":{
                       |      "@id":"2950159",
                       |      "@name":"Berlin",
                       |      "@state":"Berlin",
                       |      "@stateCode":"16",
                       |      "coords":{
                       |       "@lat":"52.5166667",
                       |       "@long":"13.4"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/astra-berlin-germany-73d63225.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/astra-berlin-germany-3bf5f448.html"
                       |   },
                       |   {
                       |    "@eventDate":"13-09-2015",
                       |    "@id":"23f4a4f3",
                       |    "@lastUpdated":"2015-09-15T16:40:27.000+0000",
                       |    "@versionId":"63b1966b",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"6bd63a4e",
                       |     "@name":"Luxor",
                       |     "city":{
                       |      "@id":"2886242",
                       |      "@name":"Cologne",
                       |      "@state":"North Rhine-Westphalia",
                       |      "@stateCode":"07",
                       |      "coords":{
                       |       "@lat":"50.9333333",
                       |       "@long":"6.95"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/luxor-cologne-germany-6bd63a4e.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Pulse\/Surreal"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/luxor-cologne-germany-23f4a4f3.html"
                       |   },
                       |   {
                       |    "@eventDate":"09-09-2015",
                       |    "@id":"5bf75b28",
                       |    "@lastUpdated":"2015-09-07T09:46:08.000+0000",
                       |    "@versionId":"bb015de",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"2bd634da",
                       |     "@name":"Turock",
                       |     "city":{
                       |      "@id":"2928810",
                       |      "@name":"Essen",
                       |      "@state":"North Rhine-Westphalia",
                       |      "@stateCode":"07",
                       |      "coords":{
                       |       "@lat":"51.45",
                       |       "@long":"7.0166667"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/turock-essen-germany-2bd634da.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/turock-essen-germany-5bf75b28.html"
                       |   },
                       |   {
                       |    "@eventDate":"08-09-2015",
                       |    "@id":"3f4b17b",
                       |    "@lastUpdated":"2015-09-13T15:54:13.000+0000",
                       |    "@versionId":"3b1b52f",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"13d61d21",
                       |     "@name":"Universum",
                       |     "city":{
                       |      "@id":"2825297",
                       |      "@name":"Stuttgart",
                       |      "@state":"Baden-Württemberg",
                       |      "@stateCode":"01",
                       |      "coords":{
                       |       "@lat":"48.7823242931971",
                       |       "@long":"9.17701721191406"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/universum-stuttgart-germany-13d61d21.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Pulse\/Surreal"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/universum-stuttgart-germany-3f4b17b.html"
                       |   },
                       |   {
                       |    "@eventDate":"07-09-2015",
                       |    "@id":"23f75063",
                       |    "@lastUpdated":"2015-09-08T12:09:19.000+0000",
                       |    "@versionId":"6bb00aaa",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"5bd62318",
                       |     "@name":"Das Bett",
                       |     "city":{
                       |      "@id":"2925533",
                       |      "@name":"Frankfurt",
                       |      "@state":"Hesse",
                       |      "@stateCode":"05",
                       |      "coords":{
                       |       "@lat":"50.1166667",
                       |       "@long":"8.6833333"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/das-bett-frankfurt-germany-5bd62318.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Pulse\/Surreal"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/das-bett-frankfurt-germany-23f75063.html"
                       |   },
                       |   {
                       |    "@eventDate":"06-09-2015",
                       |    "@id":"3bf75814",
                       |    "@lastUpdated":"2015-09-07T05:00:48.000+0000",
                       |    "@versionId":"43b01b97",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"3d7dd9f",
                       |     "@name":"Feierwerk (Kranhalle)",
                       |     "city":{
                       |      "@id":"2867714",
                       |      "@name":"Munich",
                       |      "@state":"Bavaria",
                       |      "@stateCode":"02",
                       |      "coords":{
                       |       "@lat":"48.1374325498109",
                       |       "@long":"11.5754914283752"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/feierwerk-kranhalle-munich-germany-3d7dd9f.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/feierwerk-kranhalle-munich-germany-3bf75814.html"
                       |   },
                       |   {
                       |    "@eventDate":"14-08-2015",
                       |    "@id":"63f78293",
                       |    "@lastUpdated":"2015-08-16T20:09:16.000+0000",
                       |    "@versionId":"43b24b83",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"2bd6145a",
                       |     "@name":"Flugplatz des Aeroclubs Dinkelsbühl",
                       |     "city":{
                       |      "@id":"2936886",
                       |      "@name":"Dinkelsbühl",
                       |      "@state":"Bavaria",
                       |      "@stateCode":"02",
                       |      "coords":{
                       |       "@lat":"49.0694238139101",
                       |       "@long":"10.3198528289795"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/flugplatz-des-aeroclubs-dinkelsbuhl-dinkelsbuhl-germany-2bd6145a.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Pulse\/Surreal"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/flugplatz-des-aeroclubs-dinkelsbuhl-dinkelsbuhl-germany-63f78293.html"
                       |   },
                       |   {
                       |    "@eventDate":"07-08-2015",
                       |    "@id":"53f7bfc9",
                       |    "@lastUpdated":"2016-05-17T22:40:57.000+0000",
                       |    "@versionId":"1ba03dac",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"7bd6361c",
                       |     "@name":"Vojenská pevnost Josefov",
                       |     "city":{
                       |      "@id":"3074463",
                       |      "@name":"Jaromer",
                       |      "@stateCode":"79",
                       |      "coords":{
                       |       "@lat":"48.7",
                       |       "@long":"14.533333"
                       |      },
                       |      "country":{
                       |       "@code":"CZ",
                       |       "@name":"Czech Republic"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/vojenska-pevnost-josefov-jaromer-czech-republic-7bd6361c.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Pulse\/Surreal"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/vojenska-pevnost-josefov-jaromer-czech-republic-53f7bfc9.html"
                       |   },
                       |   {
                       |    "@eventDate":"28-02-2015",
                       |    "@id":"2bcb902e",
                       |    "@lastUpdated":"2015-03-01T23:37:28.000+0000",
                       |    "@versionId":"6381ca8f",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"1bd465c8",
                       |     "@name":"Sala Paddock",
                       |     "city":{
                       |      "@id":"3117735",
                       |      "@name":"Madrid",
                       |      "@state":"Autonomous Region of Madrid",
                       |      "@stateCode":"29",
                       |      "coords":{
                       |       "@lat":"40.4165020941502",
                       |       "@long":"-3.70256423950195"
                       |      },
                       |      "country":{
                       |       "@code":"ES",
                       |       "@name":"Spain"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/sala-paddock-madrid-spain-1bd465c8.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Intrauterin"
                       |       },
                       |       {
                       |        "@name":"Bloody Lips And Paper Skin"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun III: Aquamarine Towers"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Bliss"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/sala-paddock-madrid-spain-2bcb902e.html"
                       |   },
                       |   {
                       |    "@eventDate":"27-02-2015",
                       |    "@id":"33cb1089",
                       |    "@lastUpdated":"2016-08-16T16:48:12.000+0000",
                       |    "@versionId":"4baa9f3e",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"2bd4e0ce",
                       |     "@name":"RCA Club",
                       |     "city":{
                       |      "@id":"2267057",
                       |      "@name":"Lisbon",
                       |      "@state":"Lisbon",
                       |      "@stateCode":"14",
                       |      "coords":{
                       |       "@lat":"38.7166667",
                       |       "@long":"-9.1333333"
                       |      },
                       |      "country":{
                       |       "@code":"PT",
                       |       "@name":"Portugal"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/rca-club-lisbon-portugal-2bd4e0ce.html"
                       |    },
                       |    "sets":{
                       |     "set":{
                       |      "song":[
                       |       {
                       |        "@name":"Intrauterin"
                       |       },
                       |       {
                       |        "@name":"Bloody Lips And Paper Skin"
                       |       },
                       |       {
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       },
                       |       {
                       |        "@name":"Melting Sun II: Cherry Quartz"
                       |       },
                       |       {
                       |        "@name":"Melting Sun III: Aquamarine Towers"
                       |       },
                       |       {
                       |        "@name":"Melting Sun IV: Jade Fields"
                       |       },
                       |       {
                       |        "@name":"Coma"
                       |       }
                       |      ]
                       |     }
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2015\/rca-club-lisbon-portugal-33cb1089.html"
                       |   },
                       |   {
                       |    "@eventDate":"04-10-2014",
                       |    "@id":"63cf420f",
                       |    "@lastUpdated":"2014-10-05T18:50:10.000+0000",
                       |    "@tour":"Waves of Light Tour 2014",
                       |    "@versionId":"13985925",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"3bd4b0d0",
                       |     "@name":"Helvete Metal Club",
                       |     "city":{
                       |      "@id":"2860416",
                       |      "@name":"Oberhausen",
                       |      "@state":"Bavaria",
                       |      "@stateCode":"02",
                       |      "coords":{
                       |       "@lat":"49.55",
                       |       "@long":"9.9333333"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/helvete-metal-club-oberhausen-germany-3bd4b0d0.html"
                       |    },
                       |    "sets":{
                       |     "set":[
                       |      {
                       |       "song":[
                       |        {
                       |         "@name":"Intrauterin"
                       |        },
                       |        {
                       |         "@name":"Bloody Lips And Paper Skin"
                       |        },
                       |        {
                       |         "@name":"Melting Sun I: Azure Chimes"
                       |        },
                       |        {
                       |         "@name":"Melting Sun II: Cherry Quartz"
                       |        },
                       |        {
                       |         "@name":"Melting Sun III: Aquamarine Towers"
                       |        },
                       |        {
                       |         "@name":"Melting Sun IV: Jade Fields"
                       |        },
                       |        {
                       |         "@name":"Bliss"
                       |        },
                       |        {
                       |         "@name":"Coma"
                       |        }
                       |       ]
                       |      },
                       |      {
                       |       "@encore":"1",
                       |       "song":{
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       }
                       |      }
                       |     ]
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/helvete-metal-club-oberhausen-germany-63cf420f.html"
                       |   },
                       |   {
                       |    "@eventDate":"02-10-2014",
                       |    "@id":"23cf608b",
                       |    "@lastFmEventId":"3895531",
                       |    "@lastUpdated":"2014-10-02T02:31:56.000+0000",
                       |    "@versionId":"2b982816",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"33d6c8e9",
                       |     "@name":"K17",
                       |     "city":{
                       |      "@id":"2950159",
                       |      "@name":"Berlin",
                       |      "@state":"Berlin",
                       |      "@stateCode":"16",
                       |      "coords":{
                       |       "@lat":"52.5166667",
                       |       "@long":"13.4"
                       |      },
                       |      "country":{
                       |       "@code":"DE",
                       |       "@name":"Germany"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/k17-berlin-germany-33d6c8e9.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/k17-berlin-germany-23cf608b.html"
                       |   },
                       |   {
                       |    "@eventDate":"01-10-2014",
                       |    "@id":"bcf6536",
                       |    "@lastFmEventId":"3882266",
                       |    "@lastUpdated":"2014-10-01T02:30:16.000+0000",
                       |    "@versionId":"139835e5",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"2bd604da",
                       |     "@name":"Chapeau Rouge",
                       |     "city":{
                       |      "@id":"3067696",
                       |      "@name":"Prague",
                       |      "@state":"Hlavní Mesto Praha",
                       |      "@stateCode":"52",
                       |      "coords":{
                       |       "@lat":"50.0878367932108",
                       |       "@long":"14.4241322001241"
                       |      },
                       |      "country":{
                       |       "@code":"CZ",
                       |       "@name":"Czech Republic"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/chapeau-rouge-prague-czech-republic-2bd604da.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/chapeau-rouge-prague-czech-republic-bcf6536.html"
                       |   },
                       |   {
                       |    "@eventDate":"30-09-2014",
                       |    "@id":"53cf734d",
                       |    "@lastUpdated":"2014-09-29T12:29:57.000+0000",
                       |    "@versionId":"3b98d014",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"4bd66fe2",
                       |     "@name":"Escape Metalcorner",
                       |     "city":{
                       |      "@id":"2761369",
                       |      "@name":"Vienna",
                       |      "@state":"Vienna",
                       |      "@stateCode":"09",
                       |      "coords":{
                       |       "@lat":"48.2084877601653",
                       |       "@long":"16.3720750808716"
                       |      },
                       |      "country":{
                       |       "@code":"AT",
                       |       "@name":"Austria"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/escape-metalcorner-vienna-austria-4bd66fe2.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/escape-metalcorner-vienna-austria-53cf734d.html"
                       |   },
                       |   {
                       |    "@eventDate":"29-09-2014",
                       |    "@id":"3bcf7014",
                       |    "@lastFmEventId":"3913026",
                       |    "@lastUpdated":"2016-04-23T13:30:25.000+0000",
                       |    "@versionId":"7ba39ac8",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"63d64233",
                       |     "@name":"Klub Gromka",
                       |     "city":{
                       |      "@id":"3196359",
                       |      "@name":"Ljubljana",
                       |      "@state":"Ljubljana",
                       |      "@stateCode":"61",
                       |      "coords":{
                       |       "@lat":"46.0552778",
                       |       "@long":"14.5144444"
                       |      },
                       |      "country":{
                       |       "@code":"SI",
                       |       "@name":"Slovenia"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/klub-gromka-ljubljana-slovenia-63d64233.html"
                       |    },
                       |    "sets":{
                       |     "set":[
                       |      {
                       |       "song":[
                       |        {
                       |         "@name":"Intrauterin"
                       |        },
                       |        {
                       |         "@name":"Bloody Lips And Paper Skin"
                       |        },
                       |        {
                       |         "@name":"Melting Sun I: Azure Chimes"
                       |        },
                       |        {
                       |         "@name":"Melting Sun II: Cherry Quartz"
                       |        },
                       |        {
                       |         "@name":"Melting Sun III: Aquamarine Towers"
                       |        },
                       |        {
                       |         "@name":"Melting Sun IV: Jade Fields"
                       |        },
                       |        {
                       |         "@name":"Bliss"
                       |        },
                       |        {
                       |         "@name":"Coma"
                       |        }
                       |       ]
                       |      },
                       |      {
                       |       "@encore":"1",
                       |       "song":{
                       |        "@name":"Melting Sun I: Azure Chimes"
                       |       }
                       |      }
                       |     ]
                       |    },
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/klub-gromka-ljubljana-slovenia-3bcf7014.html"
                       |   },
                       |   {
                       |    "@eventDate":"28-09-2014",
                       |    "@id":"33cf78b9",
                       |    "@lastFmEventId":"3904861",
                       |    "@lastUpdated":"2014-09-28T08:32:19.000+0000",
                       |    "@versionId":"4b98ebce",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"2bd63436",
                       |     "@name":"Traffic",
                       |     "city":{
                       |      "@id":"3169070",
                       |      "@name":"Rome",
                       |      "@state":"Lazio",
                       |      "@stateCode":"07",
                       |      "coords":{
                       |       "@lat":"41.9",
                       |       "@long":"12.483"
                       |      },
                       |      "country":{
                       |       "@code":"IT",
                       |       "@name":"Italy"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/traffic-rome-italy-2bd63436.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/traffic-rome-italy-33cf78b9.html"
                       |   },
                       |   {
                       |    "@eventDate":"27-09-2014",
                       |    "@id":"3cf7da3",
                       |    "@lastFmEventId":"3902935",
                       |    "@lastUpdated":"2014-09-27T05:42:09.000+0000",
                       |    "@versionId":"4398f7a7",
                       |    "artist":{
                       |     "@disambiguation":"",
                       |     "@mbid":"e4aefc18-f1d3-4777-a7d1-22871dc56030",
                       |     "@name":"Lantlôs",
                       |     "@sortName":"Lantlôs",
                       |     "url":"http:\/\/www.setlist.fm\/setlists\/lantlos-5bd7fb64.html"
                       |    },
                       |    "venue":{
                       |     "@id":"43d74bdb",
                       |     "@name":"Freakout Club",
                       |     "city":{
                       |      "@id":"3181928",
                       |      "@name":"Bologna",
                       |      "@state":"Emilia-Romagna",
                       |      "@stateCode":"05",
                       |      "coords":{
                       |       "@lat":"44.4938114812334",
                       |       "@long":"11.3387489318848"
                       |      },
                       |      "country":{
                       |       "@code":"IT",
                       |       "@name":"Italy"
                       |      }
                       |     },
                       |     "url":"http:\/\/www.setlist.fm\/venue\/freakout-club-bologna-italy-43d74bdb.html"
                       |    },
                       |    "sets":"",
                       |    "url":"http:\/\/www.setlist.fm\/setlist\/lantlos\/2014\/freakout-club-bologna-italy-3cf7da3.html"
                       |   }
                       |  ]
                       | }
                       |}
                       |""".stripMargin
}