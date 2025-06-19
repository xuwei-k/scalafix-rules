/*
rule = ImplicitClassNoParent
 */
package fix

object ImplicitClassNoParentTest {
  trait A extends Any

  implicit class B0(val a: Int) extends AnyVal with A // assert: ImplicitClassNoParent

  implicit class B1(a: Int) extends A // assert: ImplicitClassNoParent

  class B2(a: Int) extends A

  class B3(val a: Int) extends AnyVal

  class B4(val a: Int) extends scala.AnyVal

  class B5(val a: Int) extends _root_.scala.AnyVal
}
