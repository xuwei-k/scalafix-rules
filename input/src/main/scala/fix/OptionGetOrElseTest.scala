/*
rule = OptionGetOrElse
 */
package fix

object OptionGetOrElseTest {
  def f1[A](a: Option[A], b: A): A =
    a match {
      case Some(c) =>
        c
      case None =>
        b
    }

  def f2[A](a: Option[A], b: A): A =
    a match {
      case Some(c) =>
        c
      case _ =>
        println("None")
        b
    }

  def f3(a: Option[Int]): Int = {
    val d = a match {
      case Some(c) =>
        c
      case _ =>
        return 3
    }
    d + 4
  }
}
