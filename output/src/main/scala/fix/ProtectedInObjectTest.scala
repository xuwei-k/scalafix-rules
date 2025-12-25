package fix

object ProtectedInObjectTest {
  private def x1 = 2
  private val x2 = 2
  private def x3 = 2
  protected[fix] def x4 = 2

  private trait A1

  private class A2

  object B {
    protected def y1 = 2
    protected val y2 = 2

    protected trait C1

    protected class C2
  }
}

trait ProtectedInObjectTestTrait

object ProtectedInObjectTest2 extends ProtectedInObjectTestTrait {
  protected def x1 = 2
  protected val x2 = 2

  protected trait A1

  protected class A2
}
