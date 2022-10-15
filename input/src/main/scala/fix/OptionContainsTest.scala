/*
rule = OptionContains
 */
package fix

object OptionContainsTest {
  def f1[A](a: Option[A], b: A): Boolean =
    a match {
      case Some(c) => c == b
      case None => false
    }

  def f2[A](a: Option[A], b: A): Boolean =
    a match {
      case Some(c) => b == c
      case _ => false
    }
}
