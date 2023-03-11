/*
rule = NamedParamOrder
 */
package fix

object NamedParamOrderTest {
  def foo(`class`: Int, `type`: Int, x: Int): Int = x

  val z1 = foo(
    x = 3,
    `class` = 1,
    `type` = 2,
  )

  scala.math.max(y = 2, x = 1)

  "a".sliding(step = 2, size = 1)

  Tuple3(_3 = 3, _2 = 2, _1 = 1)

  class A1(x1: Int, x2: String)

  def z2 = new A1(x2 = "2", x1 = 1)

  def z3 = foo(
    x = 3,
    `class` = 1, // foo
    `type` = 2,
  )

  def z4 = foo(
    x = 1,
    `class` = 2,
    `type` = foo(
      x = 3,
      `class` = 4,
      `type` = 5
    )
  )
}
