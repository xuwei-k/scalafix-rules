package fix

object NamedParamOrderTest {
  def foo(x1: Int, x2: Int, x3: Int): Int = x1

  val x1 = foo(
    x1 = 1,
    x2 = 2,
    x3 = 3,
  )

  scala.math.max(x = 1, y = 2)

  "a".sliding(size = 1, step = 2)
}
