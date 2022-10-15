/*
rule = OptionForallExists
 */
package fix

class OptionForallExistsTest {
  def f1[A](x: Option[A], g: A => Boolean): Boolean =
    x match {
      case Some(y) =>
        g(y)
      case None =>
        true
    }

  def f2[A](x: Option[A], g: A => Boolean): Boolean =
    x match {
      case Some(y) =>
        g(y)
      case _ =>
        false
    }

  def f3[A](x: Option[A], g: A => Boolean): Boolean =
    x match {
      case Some(y) =>
        println(y)
        g(y)
      case _ =>
        false
    }
}
