/*
rule = EitherMap
 */
package fix

object EitherMapTest {
  def a1(x: Either[Int, String]): Either[Int, Boolean] =
    x match {
      case Right(y) => Right(y.size == 1)
      case Left(z) => Left(z)
    }

  def a2(x: Either[Int, String]): Either[Int, Boolean] =
    x match {
      case Left(z) => Left(z)
      case Right(y) => Right(y.size == 2)
    }

  def a3(x: Either[Int, String]): Either[Boolean, String] =
    x match {
      case Left(y) => Left(y == 3)
      case Right(z) => Right(z)
    }

  def a4(x: Either[Int, String]): Either[Boolean, String] =
    x match {
      case Right(z) => Right(z)
      case Left(y) => Left(y == 4)
    }
}
