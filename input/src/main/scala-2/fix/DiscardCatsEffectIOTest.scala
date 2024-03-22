/*
rule = DiscardCatsEffectIO
 */
package fix

import cats.effect.IO
import scala.concurrent.ExecutionContext
import org.mockito.Mockito
import org.mockito.Mockito.verify

trait DiscardCatsEffectIOTest {
  def f0[R]: IO[Int]

  def f1[R](n: Int): IO[Int]

  def mock: DiscardCatsEffectIOTest

  def g[A](x: Option[IO[A]]): Option[Int] = {
    x.map(y => 3) // assert: DiscardCatsEffectIO
    x.map(_ => 3) // TODO
    x.map(implicit y => 3)
    x.map { y =>
      println(y)
      4
    }
  }

  f0[Int] // assert: DiscardCatsEffectIO

  def f2[R](implicit ec: ExecutionContext): IO[Int] = {
    f0[R] // assert: DiscardCatsEffectIO

    f1[R](2) // assert: DiscardCatsEffectIO

    f1[R](3).map(_ + 4) // assert: DiscardCatsEffectIO

    val x1 = f1[R](5)
    def x2: IO[Int] = f1[R](5)

    Mockito.verify(mock).f1(3)
    Mockito.verify(mock).f1[R](3)
    Mockito.verify(mock, Mockito.times(2)).f1(9)
    Mockito.verify(mock, Mockito.times(2)).f1[R](9)
    Mockito.verify(mock, Mockito.never()).f1(-1)
    Mockito.verify(mock, Mockito.never()).f1[R](-1)

    verify(mock).f0[R]
    verify(mock).f1(4)
    verify(mock).f1[R](4)
    verify(mock, Mockito.times(2)).f1(5)
    verify(mock, Mockito.times(2)).f1[R](5)
    verify(mock, Mockito.never()).f1(8)
    verify(mock, Mockito.never()).f1[R](8)

    x1.flatMap(y => x1.map(_ + y)) // assert: DiscardCatsEffectIO

    x1 // assert: DiscardCatsEffectIO

    var mutableValue: IO[Int] = x1

    mutableValue = x1

    x1
  }
}
