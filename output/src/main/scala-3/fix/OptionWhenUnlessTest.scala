package fix

object OptionWhenUnlessTest {
  def x1(a: Int): Option[Int] = {
    Option.when(a % 2 == 0)(a + 8)
  }

  def x2(a: Int): Option[String] = Option.when(a % 5 == 2)(a.toString)

  def y1(a: Int): Option[Int] = {
    Option.unless(a % 3 == 1)(a - 9)
  }

  def y2(a: Int): Option[String] = Option.unless(a % 4 == 1)(a.toString)
}
