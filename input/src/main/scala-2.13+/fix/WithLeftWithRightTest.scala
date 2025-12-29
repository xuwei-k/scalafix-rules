/*
rule = WithLeftWithRight
 */
package fix

trait WithLeftWithRightTest {
  def x1: Either[String, Int] = Right[String, Int](2)
  def x2: Either[String, Int] = Left[String, Int]("a")
}
