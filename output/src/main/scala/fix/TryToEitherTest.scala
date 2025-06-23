package fix

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object TryToEitherTest {
  def f1[A](x: Try[A]): Either[Throwable, A] =
    x.toEither

  def f2[A](x: Try[A]): Either[Throwable, A] =
    x.toEither

  def f3[A](x: Try[A]): Either[A, Throwable] =
    x match {
      case Success(a) => Left(a)
      case Failure(e) => Right(e)
    }

  def f4[A](x: Try[A]): Either[Throwable, A] =
    (x map (y => y)).toEither
}
