package fix

/*
rule = ExplicitImplicitTypes
 */
class ExplicitImplicitTypesTest {
  def this(x: Int) = {
    this()
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2
    implicit lazy val d3 = 2
  }

  implicit def a1 = 2 // assert: ExplicitImplicitTypes
  implicit val a2 = 2 // assert: ExplicitImplicitTypes
  implicit lazy val a3 = 2 // assert: ExplicitImplicitTypes

  private[this] implicit def b1 = 2 // assert: ExplicitImplicitTypes
  private[this] implicit val b2 = 2 // assert: ExplicitImplicitTypes
  private[this] implicit lazy val b3 = 2 // assert: ExplicitImplicitTypes

  implicit def c1: Int = 2
  implicit val c2: Int = 2
  implicit lazy val c3: Int = 2

  def x1: Int = {
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2
    implicit lazy val d3 = 2
    1
  }

  val x2: Int = {
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2
    implicit lazy val d3 = 2
    2
  }

  var x3: Int = {
    implicit def d1 = 2 // assert: ExplicitImplicitTypes
    implicit val d2 = 2
    implicit lazy val d3 = 2
    3
  }

  { (a: Int) =>
    implicit def d1 = 3 // assert: ExplicitImplicitTypes
    implicit val d2 = 4
    a
  }

  locally {
    implicit def a1 = 3 // assert: ExplicitImplicitTypes
    implicit val a2 = 4
  }
}
