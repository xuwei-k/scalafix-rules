package fix

class OptionForallExistsTest {
  def f1[A](x: Option[A], g: A => Boolean): Boolean =
    x.forall(y => g(y))

  def f2[A](x: Option[A], g: A => Boolean): Boolean =
    x.exists(y => g(y))

  def f3[A](x: Option[A], g: A => Boolean): Boolean =
    x.exists{y => println(y)
        g(y)}
}
