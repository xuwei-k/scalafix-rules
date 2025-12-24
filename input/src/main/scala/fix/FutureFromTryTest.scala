/*
rule = FutureFromTry
 */
package fix

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object FutureFromTryTest {
  def f1[A](x: Try[A]): Future[A] =
    x match {
      case Failure(err) =>
        Future.failed(err)
      case Success(a) =>
        Future.successful(a)
    }

  def f2[A](x: Try[A]): Future[A] =
    x match {
      case Success(a) =>
        Future.successful(a)
      case Failure(err) =>
        Future.failed(err)
    }

  def f3[A](x: Try[A])(implicit ec: ExecutionContext): Future[A] =
    x match {
      case Success(a) =>
        Future(a)
      case Failure(err) =>
        Future.failed(err)
    }
}
