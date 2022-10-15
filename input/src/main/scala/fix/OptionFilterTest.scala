/*
rule = OptionFilter
 */
package fix

object OptionFilterTest {
  def x1(a: Option[Int]): Option[Int] = a match {
    case Some(b) if b % 2 == 0 => Some(b)
    case _ => None
  }
}
