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

  implicit class A7(a: Int) {
    import scala.util.Random

    type X = Random

    def f: X = Random
  }

  implicit class ByName(a: => Int)

  object X1 {
    object X2 {
      object X3 {
        implicit class Z(a: Int) {
          def b = a
        }
      }
    }
    class Y1 {
      object Y2 {
        implicit class Z(a: Int) {
          def b = a
        }
      }
    }
  }

  implicit class HasContextBound[A: Option](val x: Int)
}
