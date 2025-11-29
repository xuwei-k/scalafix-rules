package fix

object SortedMaxMinTest2 {
  def f1(x: List[Int]): Option[Int] = x.minOption
  def f2(x: Vector[Int]): Option[Int] = x.maxOption
}
