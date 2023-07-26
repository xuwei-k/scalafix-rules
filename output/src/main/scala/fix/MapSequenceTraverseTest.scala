package fix

import cats.syntax.all.*

object MapSequenceTraverseTest {
  def withParens: Option[List[Int]] = List(1, 2, 3).traverse(a => if (a % 2 == 0) Some(a) else None)
  def withBraces: Option[List[Int]] = List(1, 2, 3).traverse{ a => if (a % 2 == 0) Some(a) else None }
  def withParensThenBraces: Option[List[Int]] = List(1, 2, 3).traverse{ a => if (a % 2 == 0) Some(a) else None }
}
