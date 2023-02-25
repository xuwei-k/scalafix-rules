package fix

object FilterSizeTest {
  def f1[A](xs: Seq[A], f: A => Boolean): Int = xs.count(f)
  def f2(xs: Seq[Boolean]): Int = xs.count(x => x)
}
