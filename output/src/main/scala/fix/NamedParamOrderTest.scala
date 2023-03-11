package fix

object NamedParamOrderTest {
  def foo(`class`: Int, `type`: Int, x: Int): Int = x

  val z = foo(
    `class` = 1,
    `type` = 2,
    x = 3,
  )

  scala.math.max(x = 1, y = 2)

  "a".sliding(size = 1, step = 2)
}
