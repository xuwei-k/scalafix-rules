package fix

object EitherMapTest {
  def a1(x: Either[Int, String]): Either[Int, Boolean] =
    x.map(y => y.size == 1)

  def a2(x: Either[Int, String]): Either[Int, Boolean] =
    x.map(y => y.size == 2)

  def a3(x: Either[Int, String]): Either[Boolean, String] =
    x.left.map(y => y == 3)

  def a4(x: Either[Int, String]): Either[Boolean, String] =
    x.left.map(y => y == 4)
}
