/*
rule = KindProjector
 */
package fix

import cats.data.Validated

trait KindProjectorTest {
  def f1: Functor[String Either *]
  def f2: Functor[Int Validated *]
}

trait Functor[F[_]]
