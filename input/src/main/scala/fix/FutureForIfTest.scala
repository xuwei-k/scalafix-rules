/*
rule = FutureForIf
 */
package fix

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class FutureForIfTest {
  def f1[A](x1: Future[A])(implicit ec: ExecutionContext) = {
    for {
      a1 <- x1
      if true // assert: FutureForIf
    } yield a1
  }
}
