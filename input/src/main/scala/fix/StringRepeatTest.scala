/*
rule = StringRepeat
 */
package fix

import scala.annotation.unused

object StringRepeatTest {
  @unused
  private implicit class StringRepeatCompat(self: String) {
    def repeat(n: Int): String = ???
  }

  def x1: String = (2 to 10).map(a => "a").mkString
  def x2: String = (2 until 20).map(_ => "a").mkString
  def x3: String = (2 until 20).map(_ => "a").mkString(",")

  def y1: String = List.fill(10)("a").mkString
  def y2: String = Vector.fill(100)("a").mkString
  def y3: String = Seq.fill(100)("a").mkString
  def y4: String = Seq.fill(100)("a").mkString(",")
}
