/*
rule = FilterHeadOption
 */
package fix

object FilterHeadOptionTest {
  def f1(x: Seq[Int]): Option[Int] = x.filter(_ == 2).headOption
  def f2(x: List[String]): Option[String] = x.filter { y => y != "a" }.headOption
  def f3(x: Set[Int]): Option[Int] = x.filter(_ == 3).headOption
  def f4(x: Vector[Int]): Option[Int] = x.filter(_ == 4).headOption
  def f5(x: Map[Int, Int]): Option[(Int, Int)] = x.filter(_._1 == 5).headOption
  def f6(x: Stream[Int]): Option[Int] = x.filter(_ == 6).headOption
}
