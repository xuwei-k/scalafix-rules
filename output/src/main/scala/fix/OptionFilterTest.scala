package fix

object OptionFilterTest {
  def x1(a: Option[Int]): Option[Int] = a.filter(b => b % 2 == 0)
}
