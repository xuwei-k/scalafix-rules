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

trait ScalaAppTestTrait1
trait ScalaAppTestTrait2

object ScalaAppTest2 extends App with ScalaAppTestTrait1 with ScalaAppTestTrait2 {

  println("a")

}
