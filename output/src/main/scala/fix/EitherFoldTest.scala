package fix

object EitherFoldTest {
  def f1[A, B, C](x: Either[(A, A), B], g1: A => C, g2: B => C): C = {
    x.fold({ case (a1, a2) => g1(a1)
        g1(a2) }, r => g2(r))
  }

  def f2[A, B, C](x: Either[A, B], g1: A => C): Unit = {
    x.fold({ l => g1(l)
        () }, _ => {})
  }

  def f3[A, B, C](x: Either[A, B], g1: A => C, g2: B => C): C = {
    x match {
      case Left(a) =>
        g1(a)
      case Right(r) =>
        (null: Either[Int, String]).fold({ l2 => println(l2 + 3)
            g2(r) }, { r2 => println(r2 + "b")
            g2(r) })
    }
  }

  def f4[A, B](x: Either[(Int, A), A], g1: A => B, g2: A => B, b: B): B = {
    x match {
      case Right(r) =>
        g2(r)
      case Left((n, r)) =>
        if (n == 2) {
          return b
        }
        g1(r)
    }
  }
}
