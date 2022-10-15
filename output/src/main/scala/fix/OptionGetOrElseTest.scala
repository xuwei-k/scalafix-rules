package fix

object OptionGetOrElseTest {
  def f1[A](a: Option[A], b: A): A =
    a.getOrElse(b)

  def f2[A](a: Option[A], b: A): A =
    a.getOrElse{println("None")
        b}
}
