/*
rule = WithFilterSemantic
 */
package fix

object WithFilterSemanticTest {
  val f: Int => Boolean = _ => true

  def x1(list: List[Int]): List[String] = list.filter(_ % 2 == 0).map(_.toString)
  def x2(list: List[Int]): List[Int] = list.filter(f).flatMap(a => List(a))
  def x3: Unit = List(1, 2, 3).filter(_ % 2 == 0).foreach(println)
  def x4(list: List[Int]): List[Int] = list.filter(f).filter(f).map(identity)

  def vector1(xs: Vector[Int]): Vector[String] = xs.filter(_ % 2 == 0).map(_.toString)
  def seq1(xs: Seq[Int]): Seq[String] = xs.filter(_ % 3 == 0).map(_.toString)
  def set1(xs: Set[Int]): Set[String] = xs.filter(_ % 4 == 0).map(_.toString)
  def option1(xs: Option[Int]): Option[String] = xs.filter(_ % 5 == 0).map(_.toString)
}
