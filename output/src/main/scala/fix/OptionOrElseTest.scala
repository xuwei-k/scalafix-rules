package fix

object OptionOrElseTest {
  def x1[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1.orElse(a2)

  def x2[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1.orElse(a2)

  def x3[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1.orElse{println("aaa")
        a2}
}
