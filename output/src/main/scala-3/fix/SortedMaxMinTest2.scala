package fix

object SortedMaxMinTest2 {
  def f1(x: List[Int]): Option[Int] = x.minOption
  def f2(x: Vector[Int]): Option[Int] = x.maxOption
  def f3(x: Seq[Int], y: Int => String): Option[Int] = x.minByOption(y)
  def f4(x: LazyList[Int], y: Int => String): Option[Int] = x.maxByOption(y)
}
