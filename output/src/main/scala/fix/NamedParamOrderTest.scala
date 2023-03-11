package fix

object NamedParamOrderTest {
  def foo(`class`: Int, `type`: Int, x: Int): Int = x

  val z1 = foo(
    `class` = 1,
    `type` = 2,
    x = 3,
  )

  scala.math.max(x = 1, y = 2)

  "a".sliding(size = 1, step = 2)

  Tuple3(_1 = 1, _2 = 2, _3 = 3)

  class A1(x1: Int, x2: String)

  def z2 = new A1(x1 = 1, x2 = "2")

  def z3 = foo(
    x = 3,
    `class` = 1, // foo
    `type` = 2,
  )

  def z4 = foo(
    x = 1,
    `class` = 2,
    `type` = foo(
      `class` = 4,
      `type` = 5,
      x = 3
    )
  )
}
