package fix

import scala.annotation.unused

object ConstructorPrivateValTest {
  case class A1(private val x1: Int)

  implicit class A2(private val x1: Int) extends AnyVal

  class A3(
      x1: Int,
      x2: Int,
    protected val x3: Int,
    @unused private val x4: Int,
    private[ConstructorPrivateValTest] val x5: Int,
    x6: Int,
    private val x7: Int,
  )(
      y1: Int,
    protected val y2: Int,
  )(implicit
    private val z1: Int,
    protected val z2: Int,
  ) {
    def foo(that: A3): Int = that.x7
  }
}
