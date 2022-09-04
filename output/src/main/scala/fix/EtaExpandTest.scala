package fix

abstract class EtaExpandTest {
  def a1(a2: (Int, Int) => Int): Int
  def a3(b1: Int, b2: Int): Int

  def x1: Int = a1(a3)
  def x2: Int = a1((c1, c2) => a3(c2, c1))
  def x3: List[Int] = List(9).map(List(2).apply)
  def x4: List[Int] = List(9).map(x => List(x).apply(x))
}
