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

    inside(SetlistfmApi.parseSetlistfmApiJson(json)) { case Right(list) =>
      list should contain theSameElementsAs List(
        "Coma", "Melting Sun I: Azure Chimes"
      )
    }
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

    inside(SetlistfmApi.parseSetlistfmApiJson(json)) { case Right(list) =>
      list should contain theSameElementsAs List(
        "Coma", "Melting Sun I: Azure Chimes"
      )
    }
  }

  "parseSetlistfmApiJson" should "parse sample 1 correctly" in {
    inside(SetlistfmApi.parseSetlistfmApiJson(setlistfmApiSample1)) { case Right(list) =>
      List("Intrauterin"
        , "Melting Sun I: Azure Chimes"
        , "Melting Sun II: Cherry Quartz"
        , "Melting Sun IV: Jade Fields"
        , "Bliss"
        , "Coma"
        , "Melting Sun III: Aquamarine Towers"
        , "Pulse/Surreal"
        , "Bloody Lips And Paper Skin"
      )
      list.size shouldBe 9
    }
  }
}
