/*
rule = MatchParentheses
 */
package fix

trait MatchParenthesesTest {
  def f1(x: List[Int]): Int = (x match {
    case Nil => ","
    case _ => x.mkString(",")
  }).length

  def g(s: String): String

  def f2(x: List[Int]): Int = g(x match {
    case Nil => ","
    case _ => x.mkString(",")
  }).length
}
