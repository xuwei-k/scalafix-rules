/*
rule = EitherFold
 */
package fix

object EitherFoldTest {
  def f1[A, B, C](x: Either[(A, A), B], g1: A => C, g2: B => C): C = {
    x match {
      case Right(r) =>
        g2(r)
      case Left((a1, a2)) =>
        g1(a1)
        g1(a2)
    }
  }

  def f2[A, B, C](x: Either[A, B], g1: A => C): Unit = {
    x match {
      case Right(_) =>
      case Left(l) =>
        g1(l)
        ()
    }
  }

  def f3[A, B, C](x: Either[A, B], g1: A => C, g2: B => C): C = {
    x match {
      case Left(a) =>
        g1(a)
      case Right(r) =>
        (null: Either[Int, String]) match {
          case Left(l2) =>
            println(l2 + 3)
            g2(r)
          case Right(r2) =>
            println(r2 + "b")
            g2(r)
        }
    }
  }
}
