/*
rule = DiscardEff
 */
package fix

import org.atnos.eff.Eff
import org.mockito.Mockito
import org.mockito.Mockito.verify

trait DiscardEffTest {
  def f0[R]: Eff[R, Int]

  def f1[R](n: Int): Eff[R, Int]

  def g[R, A](x: Option[Eff[R, A]]): Option[Int] = {
    x.map(y => 3) // assert: DiscardEff
    x.map(_ => 3) // TODO
    x.map(implicit y => 3)
    x.map { y =>
      println(y)
      4
    }
  }

  def mock: DiscardEffTest

  f0[Int] // assert: DiscardEff

  def f2[R]: Eff[R, Int] = {
    f0[R] // assert: DiscardEff

    f1[R](2) // assert: DiscardEff

    f1[R](3).map(_ + 4) // assert: DiscardEff

    val x1 = f1[R](5)
    def x2: Eff[R, Int] = f1[R](5)

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

    x1.flatMap(y => x1.map(_ + y)) // assert: DiscardEff

    var mutableValue: Eff[R, Int] = null

    mutableValue = x1

    x1 // assert: DiscardEff

    x1
  }
}
