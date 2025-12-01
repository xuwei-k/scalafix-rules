/*
rule = ReverseFind
 */
package fix

object ReverseFindTest {
  private def p[A]: A => Boolean = Function.const(true)

  def f1[A](x: List[A]): Option[A] = x.reverse.find(p)
  def f2[A](x: Vector[A]): Option[A] = x.reverse.find(p)
  def f3[A](x: Seq[A]): Option[A] = x.reverse.find(p)
  def f4[A](x: LazyList[A]): Option[A] = x.reverse.find(p)
  def f5[A](x: collection.mutable.Seq[A]): Option[A] = x.reverse.find(p)
  def f6[A](x: collection.Seq[A]): Option[A] = x.reverse.find(p)
  def f7[A](x: Array[A]): Option[A] = x.reverse.find(p)
}
