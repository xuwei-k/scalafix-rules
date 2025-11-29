/*
rule = SortedMaxMin
 */
package fix

object SortedMaxMinTest2 {
  def f1(x: List[Int]): Option[Int] = x.sorted.headOption
  def f2(x: Vector[Int]): Option[Int] = x.sorted.lastOption
}
