/*
rule = ExplicitImplicitTypes
ExplicitImplicitTypes {
  excludeLocal = false
}
 */
package fix

object ExplicitImplicitTypesTest2 {
  implicit def a1 = 2 // assert: ExplicitImplicitTypes
  implicit val a2 = 2 // assert: ExplicitImplicitTypes
  implicit lazy val a3 = 2 // assert: ExplicitImplicitTypes

  implicit def c1: Int = 2
  implicit val c2: Int = 2
  implicit lazy val c3: Int = 2

  def x1: Int = {
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2 // assert: ExplicitImplicitTypes
    implicit lazy val d3 = 2 // assert: ExplicitImplicitTypes
    1
  }

  val x2: Int = {
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2 // assert: ExplicitImplicitTypes
    implicit lazy val d3 = 2 // assert: ExplicitImplicitTypes
    2
  }

  var x3: Int = {
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2 // assert: ExplicitImplicitTypes
    implicit lazy val d3 = 2 // assert: ExplicitImplicitTypes
    3
  }
}
