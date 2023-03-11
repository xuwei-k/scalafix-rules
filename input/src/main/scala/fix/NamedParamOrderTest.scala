/*
rule = NamedParamOrder
 */
package fix

object NamedParamOrderTest {
  def foo(x1: Int, x2: Int, x3: Int): Int = x1

  val x1 = foo(
    x3 = 3,
    x1 = 1,
    x2 = 2,
  )

  scala.math.max(y = 2, x = 1)

  "a".sliding(step = 2, size = 1)
}
