package fix

object KeySetTest {
  def f[A, B](x: collection.Map[A, B]): collection.Set[A] = x.keySet
  def f[A, B](x: collection.immutable.Map[A, B]): Set[A] = x.keySet
  def f[A, B](x: collection.immutable.HashMap[A, B]): Set[A] = x.keySet
  def f[A, B](x: collection.immutable.TreeMap[A, B]): Set[A] = x.keySet
  def f[A, B](x: collection.immutable.SortedMap[A, B]): Set[A] = x.keySet
  def f[A, B](x: collection.mutable.Map[A, B]): collection.Set[A] = x.keySet
  def f[A, B](x: collection.mutable.HashMap[A, B]): collection.Set[A] = x.keySet
  def f[A, B](x: collection.mutable.TreeMap[A, B]): collection.Set[A] = x.keySet
  def f[A, B](x: collection.mutable.SortedMap[A, B]): collection.Set[A] = x.keySet
  def f[A <: AnyRef, B](x: collection.mutable.AnyRefMap[A, B]): collection.Set[A] = x.keySet
  def f[A, B](x: collection.concurrent.TrieMap[A, B]): collection.Set[A] = x.keySet
  def f[A](x: collection.immutable.IntMap[A]): Set[Int] = x.keySet
  def f[A](x: collection.immutable.LongMap[A]): Set[Long] = x.keySet
  def f[A, B](x: Seq[(A, B)]): Set[A] = x.map(_._1).toSet
}
