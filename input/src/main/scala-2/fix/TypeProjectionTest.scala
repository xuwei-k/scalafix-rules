/*
rule = TypeProjection
 */
package fix

trait TypeProjectionTest {
  type A

  def f1: TypeProjectionTest#A // assert: TypeProjection

  trait Functor[F[_]]

  type X = Functor[({ type l[x] = Either[String, x] })#l]
}
