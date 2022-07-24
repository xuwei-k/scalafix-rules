/*
rule = MapDistinctSize
 */
package fix

class MapDistinctSizeTest {
  def a: List[(Int, String)] = List((1, "a"), (2, "b"))

  def x1: Int = a.map(_._1).distinct.size
  def x2: Int = a.map(_._2).distinct.length
  def x3: Boolean = a.map(_._1).distinct.sizeIs == 2
  def x4: Boolean = a.map(_._2).distinct.lengthIs >= 2
}
