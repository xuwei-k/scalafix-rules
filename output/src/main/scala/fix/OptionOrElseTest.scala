package fix

object OptionOrElseTest {
  def x1[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1.orElse(a2)

  def x2[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1.orElse(a2)

  def x3[A](a1: Option[A], a2: Option[A]): Option[A] =
    a1.orElse{println("aaa")
        a2}

  def x4[A](a1: Option[A], a2: Option[A], a3: Int): Option[A] =
    a1 match {
      case Some(b) =>
        Some(b)
      case None =>
        if (a3 == 0) {
          return None
        }
        a2
    }

}
