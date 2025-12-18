package fix

object WithFilterSemanticTest {
  val f: Int => Boolean = _ => true

  def x1(list: List[Int]): List[String] = list.withFilter(_ % 2 == 0).map(_.toString)
  def x2(list: List[Int]): List[Int] = list.withFilter(f).flatMap(a => List(a))
  def x3: Unit = List(1, 2, 3).withFilter(_ % 2 == 0).foreach(println)
  def x4(list: List[Int]): List[Int] = list.filter(f).withFilter(f).map(identity)

  def vector1(xs: Vector[Int]): Vector[String] = xs.withFilter(_ % 2 == 0).map(_.toString)
  def seq1(xs: Seq[Int]): Seq[String] = xs.withFilter(_ % 3 == 0).map(_.toString)
  def set1(xs: Set[Int]): Set[String] = xs.withFilter(_ % 4 == 0).map(_.toString)
  def option1(xs: Option[Int]): Option[String] = xs.filter(_ % 5 == 0).map(_.toString)
}
