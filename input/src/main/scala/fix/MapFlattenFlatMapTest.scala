/*
rule = MapFlattenFlatMap
 */
package fix

object MapFlattenFlatMapTest {
  def f(x: Option[Option[Int]]): Option[Int] = x.map(x => x.filter(_ % 2 == 0)).flatten
}
