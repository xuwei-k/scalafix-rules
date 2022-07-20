package fix

import cats.syntax.all.*

object FlatTraverseTest {
  def x: Option[List[Int]] = List(1).flatTraverse(a => Option(List(a)))
}
