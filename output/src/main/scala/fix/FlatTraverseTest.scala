package fix

import cats.syntax.all.*

object FlatTraverseTest {
  def x1: Option[List[Int]] = List(1).flatTraverse(a => Option(List(a)))

  def x2: Option[List[Int]] = List(1).flatTraverse{ a => Option(List(a)) }

  def x3: Option[List[Int]] = List(1).flatTraverse{ a =>
    // comment
    Option(List(a))
  }
}
