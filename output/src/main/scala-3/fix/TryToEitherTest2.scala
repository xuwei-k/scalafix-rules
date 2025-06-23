package fix

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object TryToEitherTest2 {
  def f1[A](x: Try[A]): Either[Throwable, A] =
    x.toEither
}
