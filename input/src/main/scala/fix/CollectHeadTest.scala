/*
rule = CollectHead
 */
package fix

object CollectHeadTest {
  def x1: String = List(1, 2, 3).collect { case n if n % 2 == 0 => n.toString }.head
}
