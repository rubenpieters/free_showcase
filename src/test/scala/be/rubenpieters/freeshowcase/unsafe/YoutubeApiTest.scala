package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.util.json.FieldNotFound
import be.rubenpieters.freeshowcase.{TestSpec, Video}

/**
  * Created by ruben on 28/01/17.
  */
class YoutubeApiTest extends TestSpec {
  val youtubeApiSample1 = testString("youtubeApiSample1.txt")

  "parseYoutubeApiJson" should "parse sample 1 correctly" in {
    inside(YoutubeApi.parseYoutubeApiJson(youtubeApiSample1)) { case Right(parsed) =>
      parsed should contain theSameElementsAs List(
        Video("Why the free Monad isn't free - by Kelley Robinson","https://www.youtube.com/watch?v=U0lK0hnbc4U","U0lK0hnbc4U")
        , Video("SBTB 2015: David Hoyt, Drinking the Free Kool-Aid","https://www.youtube.com/watch?v=T4956GI-6Lw","T4956GI-6Lw")
        , Video("YOW! Lambda Jam 2014 - Run free with the monads: Free Monads for fun and profit - Ken Scrambler","https://www.youtube.com/watch?v=fU8eKoakd6o","fU8eKoakd6o")
        , Video("Kelley Robinson - Why The Free Monad isn't Free - Curry On","https://www.youtube.com/watch?v=20WVE3bkYrQ","20WVE3bkYrQ")
        , Video("A Year living Freely - Chris Myers","https://www.youtube.com/watch?v=rK53C-xyPWw","rK53C-xyPWw")
        , Video("3 13  String Diagrams for Free Monads","https://www.youtube.com/watch?v=cOjmaFbnsyY","cOjmaFbnsyY")
        , Video("Move Over Free Monads: Make Way for Free Applicatives! — John de Goes","https://www.youtube.com/watch?v=H28QqxO7Ihc","H28QqxO7Ihc")
        , Video("Composable application architecture with reasonably priced monads","https://www.youtube.com/watch?v=M258zVn4m2M","M258zVn4m2M")
        , Video("The Eff monad, one monad to rule them all by Eric Torreborre at Scalar Conf 2016:","https://www.youtube.com/watch?v=KGJLeHhsZBo","KGJLeHhsZBo")
      )
    }
  }

  "parseYoutubeApiJson" should "give appropriate error when items" in {
    val errorJson = "{}"

    YoutubeApi.parseYoutubeApiJson(errorJson) shouldEqual Left(FieldNotFound("items"))
  }

  "parseYoutubeApiJson" should "currently silently ignore unparseable items" in {
    val errorJson =
      """
        |{ "items":[
        | {
        |   "id":{"videoId":"videoId1"},
        |   "snippet":{"title":"title1"}
        | },
        | {
        |   "id":{"videoId":"videoId2"}
        | },
        | {
        |   "snippet":{"title":"title3"}
        | }
        |]}
      """.stripMargin

    inside(YoutubeApi.parseYoutubeApiJson(errorJson)) { case Right(parsed) => parsed.size shouldBe 1 }
  }

  //  reactivate once these errors are not ignored silently
  //  "parseYoutubeApiJson" should "give appropriate error when items.snippet.title is not found" in {
  //    val errorJson =
  //      """
  //        |{ "items":[
  //        | {
  //        |   "id":{"videoId":"videoId"}
  //        | }
  //        |]}
  //      """.stripMargin
  //
  //    YoutubeApi.parseYoutubeApiJson(errorJson) shouldEqual Left(FieldNotFound("items.snippet.title"))
  //  }
  //
  //  "parseYoutubeApiJson" should "give appropriate error when items.id.videoId is not found" in {
  //    val errorJson =
  //      """
  //        |{ "items":[
  //        | {
  //        |   "snippet":{"title":"title"}
  //        | }
  //        |]}
  //      """.stripMargin
  //
  //    YoutubeApi.parseYoutubeApiJson(errorJson) shouldEqual Left(FieldNotFound("items.id.videoId"))
  //  }
}
