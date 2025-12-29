package fix

class MapDistinctSizeTest {
  def a: List[(Int, String)] = List((1, "a"), (2, "b"))

  def x1: Int = a.distinctBy(_._1).size
  def x2: Int = a.distinctBy(_._2).length
  def x3: Boolean = a.distinctBy(_._1).sizeIs == 2
  def x4: Boolean = a.distinctBy(_._2).lengthIs >= 2
}
