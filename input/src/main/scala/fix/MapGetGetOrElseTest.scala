/*
rule = MapGetGetOrElse
 */
package fix

object MapGetGetOrElseTest {
  def f1[A, B](map: Map[A, B], key: A, default: B): B =
    map.get(key).getOrElse(default)

  def f2[A, B](map: collection.Map[A, B], key: A, default: B): B =
    map.get(key).getOrElse(default)

  def f3[A, B](map: collection.immutable.Map[A, B], key: A, default: B): B =
    map.get(key).getOrElse(default)

  def f4[A, B](map: collection.mutable.Map[A, B], key: A, default: B): B =
    map.get(key).getOrElse(default)

  def f5[A, B](map: collection.concurrent.TrieMap[A, B], key: A, default: B): B =
    map.get(key).getOrElse(default)
}
