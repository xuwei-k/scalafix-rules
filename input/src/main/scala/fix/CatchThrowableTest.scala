/*
rule = CatchThrowable
 */
package fix

trait CatchThrowableTest {
  def f1(x1: Int, x2: Int): Int =
    try {
      x1 + 2
    } catch {
      case _: Throwable => // assert: CatchThrowable
        x2
    }
}
