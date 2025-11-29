/*
rule = SortedMaxMin
 */
package fix

object SortedMaxMinTest {
  def f1(x: Seq[Int]): Int = x.sorted.head
  def f2(x: Seq[Int]): Int = x.sorted.last
}
