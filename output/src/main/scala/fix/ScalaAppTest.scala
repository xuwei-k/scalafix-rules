package fix

object ScalaAppTest1   {

  def main(args: Array[String]): Unit = {
  println("hello")

  // comment
  def a = 1
  }

  

  class X
}

trait ScalaAppTestTrait1
trait ScalaAppTestTrait2

object ScalaAppTest2 extends   ScalaAppTestTrait1 with ScalaAppTestTrait2 {

  def main(args: Array[String]): Unit = {
  println("a")
  }

}
