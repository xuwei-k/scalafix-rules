package fix

object OptionContainsTest {
  def f1[A](a: Option[A], b: A): Boolean =
    a.contains(b)

  def f2[A](a: Option[A], b: A): Boolean =
    a.contains(b)
}
