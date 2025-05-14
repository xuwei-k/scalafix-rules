/*
rule = FutureUnit
 */
package fix

import scala.concurrent.Future

class FutureUnitTest {
  def x1: Future[Unit] = Future.successful(())
}
