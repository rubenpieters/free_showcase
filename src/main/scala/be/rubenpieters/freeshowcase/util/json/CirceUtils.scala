package be.rubenpieters.freeshowcase.util.json

import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import io.circe.optics.JsonPath._
import io.circe.{Json, JsonObject}
import io.circe.optics.{JsonPath, JsonTraversalPath}
import monocle.{Prism, _}

/**
  * Created by ruben on 28/01/17.
  */
object CirceUtils {
  trait implicits {
    implicit class EnrichedTraversal[A](traversal: Traversal[Json, A]) {
      def getAllEither(json: Json, field: String): Either[JsonReadError, List[A]] =
        traversal.getAll(json) match {
          case Nil => Left(FieldNotFound(field))
          case l => Right(l)
        }

      def getNonEmptyEither(json: Json, field: String): Either[JsonReadError, NonEmptyList[A]] =
        Either.fromOption(traversal.getAll(json).toNel, FieldNotFound(field))

      def getNonEmptyValidated(json: Json, field: String): Validated[JsonReadError, NonEmptyList[A]] =
        Validated.fromEither(getNonEmptyEither(json, field))
    }

    implicit class EnrichedOptional[A](optional: Optional[Json, A]) {
      def getEither(json: Json, field: String): Either[JsonReadError, A] =
        Either.fromOption(optional.getOption(json), FieldNotFound(field))

      def getValidated(json: Json, field: String): Validated[JsonReadError, A] =
        Validated.fromEither(getEither(json, field))
    }

    implicit class EnrichedListJson(list: List[Json]) {
      def getObjOrArrAsListEither(jsonPath: JsonPath, field: String): Either[JsonReadError, List[Json]] =
        list.foldMap(s => jsonPath.objOrArrAsList.getEither(s, field))
    }

    implicit class UnparsedListOps[A](list: List[Either[JsonReadError, A]]) {
      def ignoreSilently = list.flatMap(_.toOption)
    }

    implicit class UnparsedEitherListOps[A](eitherList: Either[JsonReadError, List[A]]) {
      def ignoreSilently = eitherList.toOption.toList.flatten
    }

    implicit class UnparsedNelOps[A](list: NonEmptyList[Either[JsonReadError, A]]) {
      def ignoreSilently = list.toList.ignoreSilently
    }

    final lazy val jsonObjectOrArray: Prism[Json, Either[JsonObject, List[Json]]] = Prism[Json, Either[JsonObject, List[Json]]](x => x.isArray match {
      case true => x.asArray.map(Right(_))
      case false => x.asObject.map(Left(_))
    }){
      case Right(arr) => Json.fromValues(arr)
      case Left(obj) => Json.fromJsonObject(obj)
    }

    final lazy val jsonObjectOrArrayAsList: Prism[Json, List[Json]] = Prism[Json, List[Json]](
      x => x.fold(
        Option(List()),
        _ => Option(List()),
        _ => Option(List()),
        _ => Option(List()),
        arr => Option(arr),
        obj => Option(List(x))
      )
    ){
      Json.fromValues
    }

    implicit class EnrichedJsonTraversalPath(jsonTraversalPath: JsonTraversalPath) {
      final def objOrArr: Traversal[Json, Either[JsonObject, List[Json]]] = jsonTraversalPath.json composePrism jsonObjectOrArray
      final def objOrArrAsList: Traversal[Json, List[Json]] = jsonTraversalPath.json composePrism jsonObjectOrArrayAsList
    }

    implicit class EnrichedJsonPath(jsonPath: JsonPath) {
      final def objOrArr: Optional[Json, Either[JsonObject, List[Json]]] = jsonPath.json composePrism jsonObjectOrArray
      final def objOrArrAsList: Optional[Json, List[Json]] = jsonPath.json composePrism jsonObjectOrArrayAsList
    }
  }
}

sealed trait JsonReadError extends Throwable
final case class FieldNotFound(field: String) extends JsonReadError {
  override def toString: String = s"FieldNotFound($field)"
}
