package fix

object SideEffectingNullaryMethodTest {
  def f1(): Unit = println("a")
  def f2(): scala.Unit = println("a")
  def f3(): _root_.scala.Unit = println("a")
  def f4(): Unit = println("a")

  class SideEffectingNullaryMethodJavaOverride extends SideEffectingNullaryMethodTestTrait {
    override def foo: Unit = println("a")
  }
}
