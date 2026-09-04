/*
rule = MapSequenceTraverse
 */
package fix

import cats.syntax.all.*

object MapSequenceTraverseTest {
  def withParens: Option[List[Int]] = List(1, 2, 3).map(a => if (a % 2 == 0) Some(a) else None).sequence
  def withBraces: Option[List[Int]] = List(1, 2, 3).map { a => if (a % 2 == 0) Some(a) else None }.sequence
  def withParensThenBraces: Option[List[Int]] = List(1, 2, 3).map { a => if (a % 2 == 0) Some(a) else None }.sequence

  def f1: List[Vector[Option[Either[Boolean, Int]]]] =
    Vector(Option(2)).map { x => x.map(f2).sequence }.sequence

  private def f2[A](x: A): List[Either[Boolean, A]] = Nil
}
