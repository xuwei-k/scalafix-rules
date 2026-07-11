/*
rule = ScalaApp
 */
package fix

object ScalaAppTest1 extends App {

  println("hello")

  // comment
  def a = 1

  class X
}

trait ScalaAppTestTrait1 {
  def a1: Int
  def a2: Int
  protected def a3: Int
}

trait ScalaAppTestTrait2

object ScalaAppTest2 extends App with ScalaAppTestTrait1 with ScalaAppTestTrait2 {

  override def a1 = 1
  override val a2 = 2
  protected def a3 = 3
  final val a4 = 4
  private val a5 = 5
  val a6 = 6

  println("a")

}
