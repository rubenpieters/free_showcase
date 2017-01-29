package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.TestSpec

/**
  * Created by ruben on 29/01/17.
  */
class SetlistfmApiTest extends TestSpec {
  val setlistfmApiSample1 = testString("setlistfmApiSample1.txt")

  "parseSetlistfmApiJson" should "parse simple example with `set` as array correctly" in {
    val json =
      """
        |  {
        |   "setlists":{
        |    "setlist":[
        |     {
        |      "sets":{
        |       "set":[
        |        {
        |         "song":[
        |          {
        |           "@name":"Coma"
        |          }
        |         ]
        |        },
        |        {
        |         "song":{
        |          "@name":"Melting Sun I: Azure Chimes"
        |         }
        |        }
        |       ]
        |      }
        |     }
        |    ]
        |   }
        |  }
      """.stripMargin

    println(SetlistfmApi.parseSetlistfmApiJson(json))
  }

  "parseSetlistfmApiJson" should "parse simple example with empty `sets` correctly" in {
    val json =
      """
        |  {
        |   "setlists":{
        |    "setlist":[
        |     {
        |      "sets":{
        |       "set":[
        |        {
        |         "song":[
        |          {
        |           "@name":"Coma"
        |          }
        |         ]
        |        },
        |        {
        |         "song":{
        |          "@name":"Melting Sun I: Azure Chimes"
        |         }
        |        }
        |       ]
        |      }
        |     },
        |     {
        |       "sets":""
        |     }
        |    ]
        |   }
        |  }
      """.stripMargin

    println(SetlistfmApi.parseSetlistfmApiJson(json))
  }

  "parseSetlistfmApiJson" should "parse sample 1 correctly" in {
    println(SetlistfmApi.parseSetlistfmApiJson(setlistfmApiSample1))
  }
}
