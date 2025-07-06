/*
rule = EitherToTry
 */
package fix

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object EitherToTryTest {
  def f1[A](x: Either[Throwable, A]): Try[A] =
    x match {
      case Left(e) => Failure(e)
      case Right(a) => Success(a)
    }

  def f2[A](x: Either[Throwable, A]): Try[A] =
    x match {
      case Right(a) => Success(a)
      case Left(e) => Failure(e)
    }

  def f3[A](x: Either[Throwable, Throwable]): Try[Throwable] =
    x match {
      case Right(a) => Failure(a)
      case Left(e) => Success(e)
    }

  def f4[A](x: Either[Throwable, A]): Try[A] =
    x map (y => y) match {
      case Right(a) => Success(a)
      case Left(e) => Failure(e)
    }
}
