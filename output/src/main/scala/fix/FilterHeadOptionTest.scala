package fix

object FilterHeadOptionTest {
  def f1(x: Seq[Int]): Option[Int] = x.find(_ == 2)
  def f2(x: List[String]): Option[String] = x.find { y => y != "a" }
  def f3(x: Set[Int]): Option[Int] = x.find(_ == 3)
  def f4(x: Vector[Int]): Option[Int] = x.find(_ == 4)
  def f5(x: Map[Int, Int]): Option[(Int, Int)] = x.find(_._1 == 5)
  def f6(x: Stream[Int]): Option[Int] = x.find(_ == 6)
}
