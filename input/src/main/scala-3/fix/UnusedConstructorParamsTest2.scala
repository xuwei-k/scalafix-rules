/*
rule = UnusedConstructorParams
 */
package fix

class A6(val x: Int)(using x2: String)

trait TraitParamTest1(x: Int) // assert: UnusedConstructorParams

object UnusedConstructorParamsEnumTest {
  enum A1(x: Int) {
    case B extends A1(2)

    def y: Int = x
  }

  enum A2(x: Int) { // assert: UnusedConstructorParams
    case B1 extends A2(1)
    case B2 extends A2(2)
  }

  enum A3(val x: Int) {
    case B1 extends A3(3)
  }

  enum A4(var x: Int) {
    case B1 extends A4(3)
  }

  enum A5(`enum`: Int) {
    def x: Int = `enum`

    case B1 extends A5(99)
  }

  trait C(using x: Int) {
    def y: Int = x
  }

  enum A6(using x: Int) extends C {
    case B1 extends A6(using 7)
  }

}
