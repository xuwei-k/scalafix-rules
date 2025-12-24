package fix

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object FutureFromTryTest {
  def f1[A](x: Try[A]): Future[A] =
    Future.fromTry(x)

  def f2[A](x: Try[A]): Future[A] =
    Future.fromTry(x)

  def f3[A](x: Try[A])(implicit ec: ExecutionContext): Future[A] =
    Future.fromTry(x)
}
