/*
rule = PromiseFailure
 */
package fix

import scala.concurrent.Future
import scala.concurrent.Promise

class PromiseFailureTest {
  def f1(n: Int): Future[Int] = {
    val x1 = Promise[Int]() // assert: PromiseFailure
    if (n > 0) {
      x1.success(3)
    }
    x1.future
  }

  def f2(n: Int): Future[Int] = {
    val x2 = Promise[Int]()
    if (n > 0) {
      x2.success(3)
    } else {
      x2.failure(new RuntimeException("ゼロより大きくないとダメです"))
    }
    x2.future
  }
}
