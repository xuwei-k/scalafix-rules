/*
rule = UnnecessarySortRewriteSemantic
 */
package fix

object UnnecessarySortRewriteSemanticTest {
  def f1(x: Seq[Int]): Int = x.sorted.head
  def f2(x: Seq[Int]): Int = x.sorted.last
  def f3(x: List[Int], y: Int => Int): Int = x.sortBy(y).head
  def f4(x: Vector[Int], y: Int => Int): Int = x.sortBy(y).last
}
