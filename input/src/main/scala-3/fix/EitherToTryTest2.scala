/*
rule = EitherToTry
 */
package fix

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object EitherToTryTest2 {
  def f1[A](x: Either[Throwable, A]): Try[A] =
    x.match {
      case Left(e) => Failure(e)
      case Right(a) => Success(a)
    }
}
