/*
rule = RemoveStringInterpolation
 */
package fix

class RemoveStringInterpolationTest {
  def a1 = s"x y x"
  def a2 = s""
  def a3 = s" ${3} "
  def a4 = f"12345"
}
