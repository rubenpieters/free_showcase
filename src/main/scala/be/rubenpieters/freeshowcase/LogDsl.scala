package be.rubenpieters.freeshowcase

/**
  * Created by ruben on 21/01/17.
  */
sealed trait LogDsl[A]

case class Log(msg: String) extends LogDsl[Unit]
