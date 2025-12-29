package fix

object FilterHeadOptionTest2 {
  def f1(x: LazyList[Int]): Option[Int] = x.find(_ == 2)
}
