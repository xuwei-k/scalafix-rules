/*
rule = DiscardSlickDBIO
 */
package fix

import slick.dbio.DBIO
import scala.concurrent.ExecutionContext
import org.mockito.Mockito
import org.mockito.Mockito.verify

trait DiscardSlickDBIOTest {
  def f0[R]: DBIO[Int]

  def f1[R](n: Int): DBIO[Int]

  def g[A](x: Option[DBIO[A]]): Option[Int] = {
    x.map(y => 3) // assert: DiscardSlickDBIO
    x.map(_ => 3) // TODO
    x.map(implicit y => 3)
    x.map { y =>
      println(y)
      4
    }
  }

  def mock: DiscardSlickDBIOTest

  f0[Int] // assert: DiscardSlickDBIO

  def f2[R](implicit ec: ExecutionContext): DBIO[Int] = {
    f0[R] // assert: DiscardSlickDBIO

    f1[R](2) // assert: DiscardSlickDBIO

    f1[R](3).map(_ + 4) // assert: DiscardSlickDBIO

    val x1 = f1[R](5)
    def x2: DBIO[Int] = f1[R](5)

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

    x1.flatMap(y => x1.map(_ + y)) // assert: DiscardSlickDBIO

    x1 // assert: DiscardSlickDBIO

    var mutableValue: DBIO[Int] = null

    mutableValue = x1

    x1
  }
}
