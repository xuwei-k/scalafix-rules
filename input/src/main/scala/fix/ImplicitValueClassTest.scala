/*
rule = ImplicitValueClass
 */
package fix

trait ImplicitValueClassTest {
  implicit class A(a: Int)
}

object ImplicitValueClassTest {
  implicit class A1(a: Int) {
    def b = a
  }

  implicit class A2(private val a: Int) {
    def b = a
  }

  implicit class A3(private[this] val a: Int) {
    def b = a
  }

  class B

  implicit class A4(a: Int) extends B {
    def b = a
  }

  implicit class A5(a: Int) {
    val b = a
  }

  implicit class A6(a: Int) {
    println(a)
  }

  implicit class ByName(a: => Int)
}
