/*
rule = FilterNot
 */
package fix

object FilterNotTest {
  def f1[A](a: Seq[A], f: A => Boolean): Seq[A] =
    a.filter(b => !f(b))

  def f2[A](a: Seq[A], f: A => Boolean): Seq[A] =
    a.filterNot(b => !f(b))

  def f3[A](a: Seq[(Boolean, A)]): Seq[(Boolean, A)] =
    a.filter(!_._1)

  def f4[A](a: Seq[(A, Boolean)]): Seq[(A, Boolean)] =
    a.filterNot(!_._2)
}
