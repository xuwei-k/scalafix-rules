/*
rule = CollectHeadOption
 */
package fix

object CollectHeadOptionTest {
  def x1: Option[String] = List(1, 2, 3).collect { case n if n % 2 == 0 => n.toString }.headOption
  def x2(f: PartialFunction[Int, String]): Option[String] = List(1, 2, 3).collect(f).headOption
}
