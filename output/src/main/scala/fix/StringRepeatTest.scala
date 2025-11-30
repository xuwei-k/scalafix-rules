package fix

import scala.annotation.unused

object StringRepeatTest {
  @unused
  private implicit class StringRepeatCompat(self: String) {
    def repeat(n: Int): String = ???
  }

  def x1: String = "a".repeat(9)
  def x2: String = "a".repeat(18)
  def x3: String = (2 until 20).map(_ => "a").mkString(",")

  def y1: String = "a".repeat(10)
  def y2: String = "a".repeat(100)
  def y3: String = "a".repeat(100)
  def y4: String = Seq.fill(100)("a").mkString(",")

  def z1: String = "a".repeat(3)
  def z2: String = "a".repeat(201)
}
