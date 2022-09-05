/*
rule = EtaExpand
 */
package fix

abstract class EtaExpandTest {
  def a1(a2: (Int, Int) => Int): Int
  def a3(b1: Int, b2: Int): Int

  def overload(s: String): Int
  def overload(s: Int): Int

  def x1: Int = a1((c1, c2) => a3(c1, c2))
  def x2: Int = a1((c1, c2) => a3(c2, c1))
  def x3: List[Int] = List(9).map(x => List(2).apply(x))
  def x4: List[Int] = List(9).map(x => List(x).apply(x))
  def x5: List[Int] = List.empty[Int].map(c1 => a3(3, c1))
  def x6 = (b1: Int) => overload(b1)
  def x7: List[Right[Nothing, String]] = List.empty[String].map(x => Right(x))

  x6(7)
}
