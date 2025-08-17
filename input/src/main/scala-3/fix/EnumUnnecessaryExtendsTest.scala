/*
rule = EnumUnnecessaryExtends
 */
package fix

object EnumUnnecessaryExtendsTest {
  trait F

  enum A {
    case A1 extends A
    case A2
    case A3[B](x: B) extends A
    case A4 extends A with F
    case A5 extends A, F

    trait C1
    class C2 extends C1
  }

  enum D[B] {
    case D1 extends D[Int]
    case D2 extends D[String]
  }

  enum E(val x: Int) {
    case E2 extends E(2)
    case E3 extends E(3)
  }
}
