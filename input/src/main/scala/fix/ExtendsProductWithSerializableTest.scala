/*
rule = ExtendsProductWithSerializable
 */
package fix

object ExtendsProductWithSerializableTest {
  sealed abstract class A0

  object A0 {
    case class X1(a: Int) extends A0
  }

  sealed trait A1
  object A1 {
    abstract case class X1(a: Int) extends A1
    case object X2 extends A1
  }

  sealed abstract class A2(a: Int) {
    def b = a
  }
  object A2 {
    sealed case class X1(a: Int) extends A2(a)
    case object X2 extends A2(8)
  }

  sealed trait A3

  sealed abstract class A4
  object A4 {
    class X1 extends A4
  }

  sealed abstract class A5 extends A3
  object A5 {
    case class X1(a: Int) extends A5
  }

}
