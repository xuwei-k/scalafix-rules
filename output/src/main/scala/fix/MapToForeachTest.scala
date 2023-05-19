package fix

abstract class MapToForeachTest {

  def f1: List[Int]
  def g1: Int => Int
  def g2: Int => String

  class A

  val x1 = new A {
    f1.foreach(g1)

    val x2 = f1.map(g1)
  }

  def x3: List[Int] = {
    f1.foreach(g1)

    f1.foreach(g2)

    f1.map(g1)
  }

}
