/*
rule = IsEmptyNonEmpty
 */
package fix

object IsEmptyNonEmptyTest {
  def f1[A](xs: Seq[A]): Boolean = !xs.isEmpty
  def f2[A](xs: Seq[A]): Boolean = !xs.nonEmpty
}
