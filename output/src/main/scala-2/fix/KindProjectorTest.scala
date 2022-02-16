package fix

import cats.data.Validated

trait KindProjectorTest {
  def f1: Functor[Either[String, *]]
  def f2: Functor[Validated[Int, *]]
}

trait Functor[F[_]]
