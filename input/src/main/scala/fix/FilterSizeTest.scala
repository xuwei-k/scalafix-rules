/*
rule = FilterSize
 */
package fix

object FilterSizeTest {
  def f1[A](xs: Seq[A], f: A => Boolean): Int = xs.filter(f).size
  def f2(xs: Seq[Boolean]): Int = xs.filter(x => x).length
}
