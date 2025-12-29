/*
rule = OptionWhenUnless
 */
package fix

object OptionWhenUnlessTest {
  def x1(a: Int): Option[Int] = {
    if (a % 2 == 0) {
      Some(a + 8)
    } else {
      None
    }
  }

  def x2(a: Int): Option[String] = if (a % 5 == 2) Some(a.toString) else None

  def y1(a: Int): Option[Int] = {
    if (a % 3 == 1) {
      None
    } else {
      Some(a - 9)
    }
  }

  def y2(a: Int): Option[String] = if (a % 4 == 1) None else Some(a.toString)
}
