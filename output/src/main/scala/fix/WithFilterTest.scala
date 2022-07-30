package fix

object WithFilterTest {
  val list = List(1, 2, 3)

  val f: Int => Boolean = ???

  def x1: List[String] = list.withFilter(_ % 2 == 0).map(_.toString)

  def x2: List[Int] = list.withFilter(f).flatMap(a => List(a))

  def x3: Unit = List(1, 2, 3).withFilter(_ % 2 == 0).foreach(println)

  def x4: List[Int] = list.filter(f).withFilter(f).map(identity)
}
