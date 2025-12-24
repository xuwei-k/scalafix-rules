/*
rule = FlatTraverse
 */
package fix

import cats.syntax.all.*

object FlatTraverseTest {
  def x1: Option[List[Int]] = List(1).traverse(a => Option(List(a))).map(_.flatten)

  def x2: Option[List[Int]] = List(1).traverse { a => Option(List(a)) }.map(_.flatten)

  def x3: Option[List[Int]] = List(1).traverse { a =>
    // comment
    Option(List(a))
  }.map(_.flatten)
}
