/*
rule = DiscardMonixTask
 */
package fix

import monix.eval.Task
import org.mockito.Mockito
import org.mockito.Mockito.verify

trait DiscardMonixTaskTest {
  def f0[R]: Task[Int]

  def f1[R](n: Int): Task[Int]

  def g[A](x: Option[Task[A]]): Option[Int] = {
    x.map(y => 3) // assert: DiscardMonixTask
    x.map(_ => 3) // TODO
    x.map(implicit y => 3)
    x.map { y =>
      println(y)
      4
    }
  }

  def mock: DiscardMonixTaskTest

  f0[Int] // assert: DiscardMonixTask

  def f2[R]: Task[Int] = {
    f0[R] // assert: DiscardMonixTask

    f1[R](2) // assert: DiscardMonixTask

    f1[R](3).map(_ + 4) // assert: DiscardMonixTask

    val x1 = f1[R](5)
    def x2: Task[Int] = f1[R](5)

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

    x1.flatMap(y => x1.map(_ + y)) // assert: DiscardMonixTask

    x1 // assert: DiscardMonixTask

    var mutableValue: Task[Int] = null

    mutableValue = x1

    x1
  }
}
