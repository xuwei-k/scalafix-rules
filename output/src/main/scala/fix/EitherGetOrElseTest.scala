package fix

class EitherGetOrElseTest {
  def f1[A, B](x: Either[A, B], b: B): B = {
    x.getOrElse{println("left")
        b}
  }

  def f2[A, B](x: Either[A, B], a: A): A = {
    x match {
      case Right(r) =>
        println(r)
        a
      case Left(l) =>
        l
    }
  }

  def f3[A, B](x: Either[A, B], a: A): A = {
    x.swap.getOrElse(a)
  }
}
