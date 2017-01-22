package be.rubenpieters.freeshowcase.catsfree

import be.rubenpieters.freeshowcase.{Log, LogDsl}
import cats._

/**
  * Created by ruben on 21/01/17.
  */
class CatsLogPrintlnInterpreter extends (LogDsl ~> Id) {
  override def apply[A](fa: LogDsl[A]): Id[A] = fa match {
    case Log(msg) => println(msg)
  }
}
