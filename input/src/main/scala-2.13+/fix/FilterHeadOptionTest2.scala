/*
rule = FilterHeadOption
 */
package fix

object FilterHeadOptionTest2 {
  def f1(x: LazyList[Int]): Option[Int] = x.filter(_ == 2).headOption
}
