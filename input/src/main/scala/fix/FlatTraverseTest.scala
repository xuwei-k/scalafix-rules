/*
rule = FlatTraverse
 */
package fix

import cats.syntax.all.*

object FlatTraverseTest {
  def x: Option[List[Int]] = List(1).traverse(a => Option(List(a))).map(_.flatten)
}
