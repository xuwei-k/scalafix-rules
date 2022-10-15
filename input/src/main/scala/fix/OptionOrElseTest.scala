/*
rule = OptionOrElse
 */
package fix

object OptionOrElseTest {
  def x1[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1 match {
      case Some(b) => Some(b)
      case None => a2
    }

  def x2[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1 match {
      case Some(b) => Some(b)
      case _ => a2
    }

  def x3[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1 match {
      case Some(b) => Some(b)
      case _ =>
        println("aaa")
        a2
    }
}
