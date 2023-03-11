/*
rule = NamedParamOrder
 */
package fix

object NamedParamOrderTest {
  def foo(`class`: Int, `type`: Int, x: Int): Int = x

  val z = foo(
    x = 3,
    `class` = 1,
    `type` = 2,
  )

  scala.math.max(y = 2, x = 1)

  "a".sliding(step = 2, size = 1)
}
