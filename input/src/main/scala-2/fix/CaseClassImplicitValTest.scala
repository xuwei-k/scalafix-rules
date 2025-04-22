/*
rule = CaseClassImplicitVal
 */
package fix

object CaseClassImplicitValTest {
  case class A1(x1: Int)(implicit val y: Int)

  class A2(x1: Int, implicit val y: Int)

  case class A3(x1: Int)(implicit y: Int)

  case class A4(x1: Int, implicit val y: Int) // assert: CaseClassImplicitVal
}
