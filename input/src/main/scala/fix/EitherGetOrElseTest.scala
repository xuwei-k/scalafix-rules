/*
rule = EitherGetOrElse
 */
package fix

class EitherGetOrElseTest {
  def f1[A, B](x: Either[A, B], b: B): B = {
    x match {
      case Left(r) =>
        println("left")
        b
      case Right(r) =>
        r
    }
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
    x match {
      case Right(_) =>
        a
      case Left(l) =>
        l
    }
  }
}
