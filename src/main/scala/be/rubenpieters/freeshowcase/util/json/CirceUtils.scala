package be.rubenpieters.freeshowcase.util.json

import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import io.circe.Json
import monocle.{Optional, Traversal}

/**
  * Created by ruben on 28/01/17.
  */
object CirceUtils {
  trait implicits {
    implicit class EnrichedTraversal[A](traversal: Traversal[Json, A]) {
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

    implicit class UnparsedListOps[A](list: List[Either[JsonReadError, A]]) {
      def ignoreSilently = list.flatMap(_.toOption)
    }

    implicit class UnparsedNelOps[A](list: NonEmptyList[Either[JsonReadError, A]]) {
      def ignoreSilently = list.toList.ignoreSilently
    }
  }
}

sealed trait JsonReadError extends Throwable
final case class FieldNotFound(field: String) extends JsonReadError
