package be.rubenpieters.freeshowcase.util

import cats._
import cats.data._
import cats.implicits._

import scala.util.control.NonFatal

/**
  * Created by ruben on 21/01/17.
  */
object SafeWrap {
  implicit val optionMonadErr = new MonadError[Option, Throwable] {
    override def flatMap[A, B](fa: Option[A])(f: (A) => Option[B]): Option[B] =
      catsStdInstancesForOption.flatMap(fa)(f)
    override def tailRecM[A, B](a: A)(f: (A) => Option[Either[A, B]]): Option[B] =
      catsStdInstancesForOption.tailRecM(a)(f)
    override def handleErrorWith[A](fa: Option[A])(f: (Throwable) => Option[A]): Option[A] =
      catsStdInstancesForOption.handleErrorWith(fa)(_ => None)
    override def raiseError[A](e: Throwable): Option[A] =
      catsStdInstancesForOption.raiseError(())
    override def pure[A](x: A): Option[A] =
      catsStdInstancesForOption.pure(x)
  }

  val safeWrapperNothing = new SafeWrapper[Nothing]
  def safe[F[_]] = safeWrapperNothing.asInstanceOf[SafeWrapper[F]]

  final class SafeWrapper[F[_]] {
    def apply[A](f: => A, nullVal: F[A])(implicit
                                         ae: ApplicativeError[F, Throwable]): F[A] =
      try {
        f match {
          case null => nullVal
          case notNull => ae.pure(notNull)
        }
      } catch {
        case NonFatal(e) => ae.raiseError(e)
      }
  }

  val safeWithDefaultWrapperNothing = new SafeWithDefaultWrapper[Nothing]
  def safeWithDefault[F[_]] = safeWithDefaultWrapperNothing.asInstanceOf[SafeWithDefaultWrapper[F]]

  final class SafeWithDefaultWrapper[F[_]] {
    def apply[A](f: => A)(implicit
                          ae: ApplicativeError[F, Throwable]
                          , dn: DefaultNull[F[A]]
    ): F[A] = safe[F](f, dn.nullValue)
  }
}

trait DefaultNull[A] {
  def nullValue: A
}

object DefaultNull {
  implicit val defaultNullThrowable = new DefaultNull[Throwable] {
    override def nullValue: Throwable = new NullPointerException
  }

  implicit val defaultNullUnit = new DefaultNull[Unit] {
    override def nullValue: Unit = ()
  }

  implicit def defaultNullAppErr[F[_], A, E](implicit
                                             ae: ApplicativeError[F, E],
                                             dn: DefaultNull[E]) = new DefaultNull[F[A]] {
    override def nullValue: F[A] = ae.raiseError(dn.nullValue)
  }
}