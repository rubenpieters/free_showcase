package be.rubenpieters.freeshowcase.util.cats

import be.rubenpieters.freeshowcase.catsfree.CatsLogOps
import cats._
import cats.free.Free

/**
  * Created by ruben on 7/02/17.
  */
object CatsUtils {
  trait implicits {
    implicit class EnrichedFToG[F[_], G[_]](fToG: F ~> G) {
      def logCalls(logger: Any => Unit) = new CatsLogAll(logger).andThen(fToG)
      def logCallsPrintln = logCalls(println)
      def logAnswers(logger: Any => Unit) = fToG.andThen(new CatsLogAll(logger))
      def logAnswersPrintln = logAnswers(println)
    }
  }

  class CatsLogAll[F[_]](logger: Any => Unit) extends (F ~> F) {
    override def apply[A](fa: F[A]): F[A] = {
      logger(fa)
      fa
    }
  }

//  def addLoggingAll[F[_], A](f: Free[F, A])(implicit L: CatsLogOps[F]): Free[F, A] = {
//    f.fold
//  }

  class CatsLogAllPure[F[_]](implicit L: CatsLogOps[F]) extends (F ~> F) {
    override def apply[A](fa: F[A]): F[A] = {
      L.log(fa.toString)
      fa
    }
  }
}
