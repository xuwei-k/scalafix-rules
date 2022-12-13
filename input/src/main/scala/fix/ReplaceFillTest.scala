/*
rule = ReplaceFill
 */
package fix

import scala.util.Random

object ReplaceFillTest {
  def x1: Seq[String] = (2 to 10).map(a => Random.nextString(4))
  def x2: Seq[String] = (2 until 20).map(_ => Random.nextString(5))
  def x3: List[String] = (3 to 10).toList.map(_ => Random.nextString(4))
  def x4: List[String] = (4 until 20).toList.map(b => Random.nextString(5))
  def x5: Seq[String] = (2 to 10).toSeq.map(_ => Random.nextString(4))
  def x6: Seq[String] = (2 until 20).toSeq.map(b => Random.nextString(5))
  def x7: Vector[String] = (2 to 20).toVector.map(_ => Random.nextString(4))
  def x8: Vector[String] = (9 until 40).toVector.map(b => Random.nextString(5))
  def x9: Array[String] = (3 to 20).toArray.map(_ => Random.nextString(4))
  def x10: Array[String] = (6 until 32).toArray.map(b => Random.nextString(5))
  def x11: Iterator[String] = (-2 to 49).iterator.map(_ => Random.nextString(8))
  def x12: Iterator[String] = (0 until 111).iterator.map(b => Random.nextString(9))
  def x13: Seq[String] = for (i <- 1 to 10) yield Random.nextString(9876)
  def x14: List[String] = for (i <- (1 until 20).toList) yield Random.nextString(112233)
  def x15: Vector[String] = for (_ <- (-33 until 20).toVector) yield Random.nextString(7654)

  def y1: Seq[String] = (6 until 32).map(s => Random.nextString(s))
}
