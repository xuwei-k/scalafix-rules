package fix

object CollectHeadOptionTest {
  def x1: Option[String] = List(1, 2, 3).collectFirst{ case n if n % 2 == 0 => n.toString }
}
