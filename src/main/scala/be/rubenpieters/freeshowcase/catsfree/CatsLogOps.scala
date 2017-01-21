package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.{Log, LogDsl}
import cats._
import cats.free.{Free, Inject}

/**
  * Created by ruben on 21/01/17.
  */
class CatsLogOps[F[_]](implicit I: Inject[LogDsl, F]) {
  def log(msg: String) = Free.inject[LogDsl, F](Log(msg))
}

object CatsLogOps {
  implicit def logOps[F[_]](implicit I: Inject[LogDsl, F]): CatsLogOps[F] = new CatsLogOps[F]
}

class TestCatsLogInterp extends (LogDsl ~> Id) {
  override def apply[A](fa: LogDsl[A]): Id[A] = fa match {
    case Log(msg) => ()
  }
}
