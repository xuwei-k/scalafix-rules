package fix

object CollectHeadTest {
  def x1: String = List(1, 2, 3).collectFirst { case n if n % 2 == 0 => n.toString }.get
}
