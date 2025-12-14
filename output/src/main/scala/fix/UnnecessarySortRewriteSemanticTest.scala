package fix

object UnnecessarySortRewriteSemanticTest {
  def f1(x: Seq[Int]): Int = x.min
  def f2(x: Seq[Int]): Int = x.max
  def f3(x: List[Int], y: Int => Int): Int = x.minBy(y)
  def f4(x: Vector[Int], y: Int => Int): Int = x.maxBy(y)
}
