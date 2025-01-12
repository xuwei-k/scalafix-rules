package fix

trait KindProjectorScala3TypeLambdaTest {
  type X1[b] = [a] =>> Either[a, b]

  def f: X1[Int][String] = Right(2).withLeft[String]
}
