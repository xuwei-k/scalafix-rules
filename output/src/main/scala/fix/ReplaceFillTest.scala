package fix

import scala.util.Random

object ReplaceFillTest {
  def x1: Seq[String] = Seq.fill(9){Random.nextString(4)}
  def x2: Seq[String] = Seq.fill(18){Random.nextString(5)}
  def x3: List[String] = List.fill(8){Random.nextString(4)}
  def x4: List[String] = List.fill(16){Random.nextString(5)}
  def x5: Seq[String] = Seq.fill(9){Random.nextString(4)}
  def x6: Seq[String] = Seq.fill(18){Random.nextString(5)}
  def x7: Vector[String] = Vector.fill(19){Random.nextString(4)}
  def x8: Vector[String] = Vector.fill(31){Random.nextString(5)}
  def x9: Array[String] = Array.fill(18){Random.nextString(4)}
  def x10: Array[String] = Array.fill(26){Random.nextString(5)}
  def x11: Iterator[String] = Iterator.fill(52){Random.nextString(8)}
  def x12: Iterator[String] = Iterator.fill(111){Random.nextString(9)}
  def x13: Seq[String] = Seq.fill(10){Random.nextString(9876)}
  def x14: List[String] = List.fill(19){Random.nextString(112233)}
  def x15: Vector[String] = Vector.fill(53){Random.nextString(7654)}

  def y1: Seq[String] = (6 until 32).map(s => Random.nextString(s))
}
