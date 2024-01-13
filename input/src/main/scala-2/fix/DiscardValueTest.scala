/*
rule = DiscardValue
DiscardValue.error = [
  "scala/Option#"
]
 */
package fix

import org.mockito.Mockito
import org.mockito.Mockito.verify

trait DiscardValueTest {
  def f0[R]: Option[Int]

  def f1[R](n: Int): Option[Int]

  def mock: DiscardValueTest

  f0[Int] // assert: DiscardValue

  def f2[R]: Option[Int] = {
    f0[R] // assert: DiscardValue

    f1[R](2) // assert: DiscardValue

    f1[R](3).map(_ + 4) // assert: DiscardValue

    val x1 = f1[R](5)
    def x2: Option[Int] = f1[R](5)

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

    x1.flatMap(y => x1.map(_ + y)) // assert: DiscardValue

    x1 // assert: DiscardValue

    x1
  }
}
