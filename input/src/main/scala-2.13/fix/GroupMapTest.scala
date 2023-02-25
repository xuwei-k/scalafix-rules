/*
rule = GroupMap
 */
package fix

object GroupMapTest {
  def f[A, B](xs: List[(A, B)]): Map[A, List[B]] =
    xs.groupBy(_._1).view.mapValues(_.map(x => x._2)).toMap
}
