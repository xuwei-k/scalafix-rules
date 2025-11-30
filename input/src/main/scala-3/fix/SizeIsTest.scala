/*
rule = SizeIs
 */
package fix

object SizeIsTest {
  def f1[A](x: Seq[A], n: Int): Seq[Boolean] = Seq(
    x.size < n,
    x.size <= n,
    x.size > n,
    x.size >= n,
    x.size == n,
    x.size != n,
    x.length < n,
    x.length <= n,
    x.length > n,
    x.length >= n,
    x.length == n,
    x.length != n,
  )

  def f2[A](x: List[A], n: Int): Seq[Boolean] = Seq(
    x.size == n,
    x.length == n,
  )

  def f3[A](x: Vector[A], n: Int): Seq[Boolean] = Seq(
    x.size == n,
    x.length == n,
  )

  def f4[A](x: collection.Seq[A], n: Int): Seq[Boolean] = Seq(
    x.size == n,
    x.length == n,
  )

  def f5[A](x: LazyList[A], n: Int): Seq[Boolean] = Seq(
    x.size == n,
    x.length == n,
  )

  def f6[A](x: collection.mutable.Seq[A], n: Int): Seq[Boolean] = Seq(
    x.size == n,
    x.length == n,
  )

  def f7[A, B](x: Map[A, B], n: Int): Boolean = x.size == n

  def f8[A](x: Set[A], n: Int): Boolean = x.size == n

  def f9[A, B](x: collection.Map[A, B]): Boolean = x.size == 2

  def f10[A](x: collection.Set[A]): Boolean = x.size == 2

  def f11[A, B](x: collection.mutable.Map[A, B]): Boolean = x.size == 2

  def f12[A](x: collection.mutable.Set[A]): Boolean = x.size == 2

  def f13[A, B](x: collection.concurrent.TrieMap[A, B]): Boolean = x.size == 2

  trait MyClass[A] { def size: Int }

  def x1[A](x: MyClass[A], n: Int): Boolean = x.size == n
}
