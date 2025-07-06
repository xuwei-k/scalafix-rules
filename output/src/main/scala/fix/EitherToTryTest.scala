package fix

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object EitherToTryTest {
  def f1[A](x: Either[Throwable, A]): Try[A] =
    x.toTry

  def f2[A](x: Either[Throwable, A]): Try[A] =
    x.toTry

  def f3[A](x: Either[Throwable, Throwable]): Try[Throwable] =
    x match {
      case Right(a) => Failure(a)
      case Left(e) => Success(e)
    }

  def f4[A](x: Either[Throwable, A]): Try[A] =
    (x map (y => y)).toTry
}
