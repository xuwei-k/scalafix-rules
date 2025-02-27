/*
rule = ReplaceMapContains
 */
package fix

import scala.collection.concurrent.TrieMap

trait ReplaceMapContainsTest {
  def f1(m: TrieMap[Int, String], x: Int): Boolean = m.get(x).isEmpty
  def f2(m: collection.Map[Int, String], x: Int): Boolean = m.get(x).nonEmpty
  def f3(m: collection.mutable.Map[Int, String], x: Int): Boolean = m.get(x).isDefined

  def f4(m: Foo, x: Int): Boolean = m.get(x).isDefined
  def f5(m: Foo, x: Int): Boolean = m.get(x).isEmpty
  def f6(m: Foo, x: Int): Boolean = m.get(x).nonEmpty

  trait Foo {
    def get(x: Int): Option[Int]
  }
}
