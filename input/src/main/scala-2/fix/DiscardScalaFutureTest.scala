/*
rule = DiscardScalaFuture
 */
package fix

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import org.mockito.Mockito
import org.mockito.Mockito.verify

trait DiscardScalaFutureTest {
  def f0[R]: Future[Int]

  def f1[R](n: Int): Future[Int]

  def mock: DiscardScalaFutureTest

  f0[Int] // assert: DiscardScalaFuture

  def f2[R](implicit ec: ExecutionContext): Future[Int] = {
    f0[R] // assert: DiscardScalaFuture

    f1[R](2) // assert: DiscardScalaFuture

    f1[R](3).map(_ + 4) // assert: DiscardScalaFuture

    val x1 = f1[R](5)
    def x2: Future[Int] = f1[R](5)

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

    x1.flatMap(y => x1.map(_ + y)) // assert: DiscardScalaFuture

    x1 // assert: DiscardScalaFuture

    var mutableValue: Future[Int] = null

    mutableValue = x1

    x1
  }
}
