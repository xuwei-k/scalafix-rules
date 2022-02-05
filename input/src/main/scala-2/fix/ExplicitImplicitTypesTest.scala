package fix

/*
rule = ExplicitImplicitTypes
 */
object ExplicitImplicitTypesTest {
  implicit def a1 = 2 // assert: ExplicitImplicitTypes
  implicit val a2 = 2 // assert: ExplicitImplicitTypes
  implicit lazy val a3 = 2 // assert: ExplicitImplicitTypes

  private[this] implicit def b1 = 2 // assert: ExplicitImplicitTypes
  private[this] implicit val b2 = 2 // assert: ExplicitImplicitTypes
  private[this] implicit lazy val b3 = 2 // assert: ExplicitImplicitTypes

  implicit def c1: Int = 2
  implicit val c2: Int = 2
  implicit lazy val c3: Int = 2
}
