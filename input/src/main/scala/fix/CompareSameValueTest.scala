/*
rule = CompareSameValue
 */
package fix

object CompareSameValueTest {
  private implicit class Ops[A](a: A) {
    def ===[B](b: B): Boolean = ???
  }

  private implicit class Ops2[F[_], A1, A2](self: (F[A1], F[A2])) {
    def mapN[Z](f: (A1, A2) => Z): F[Z] = ???
  }

  object A1 {
    def b1 = ???
    object A2 {
      def b2(x: Int): String = ???
    }
  }

  case class X1(x: Int)

  A1.b1 == A1.b1 // assert: CompareSameValue

  A1.b1 != A1.b1 // assert: CompareSameValue

  A1 eq A1 // assert: CompareSameValue

  A1.A2.b2(3) ne A1.A2.b2(3) // assert: CompareSameValue

  def f[A <: AnyRef](xs: List[A]) = xs.head.equals(xs.head) // assert: CompareSameValue

  X1(123).x === X1(123).x // assert: CompareSameValue

  def f[F[_]](x1: F[Int], x2: F[Int]) = (x1, x2).mapN(_ % 2 == _ % 2)
}
