package fix

object ReplacePlaceholderTest {
  def x1: List[Int] = List("a1").map(_.length)
  def x2: List[Int] = List("a2").map(a => a.length + a.toInt)
  def x3: List[Char] = List("a3").map(a => a.toList(a.toInt))
  def x4: List[Char] = List("a4").map(_.toList(3))
  def x5: List[Boolean] = List("a5").map(_.length == 2)
}
