/*
rule = ForeachEntry
 */
package fix

trait ForeachEntryTest {
  def f1(xs: Map[Int, Int]): Unit = xs.foreach { case (k, v) => v }

  def f2(xs: collection.Map[Int, Int]): Unit = xs.foreach { case (_, v) => v }

  def f3(xs: collection.mutable.TreeMap[Int, Int]): Unit =
    for ((k, _) <- xs) { // assert: ForeachEntry
      print(k)
    }
}
