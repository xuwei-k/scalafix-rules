/*
rule = UnnecessaryReturn
 */
package fix

object UnnecessaryReturnTest {
  def f1(x1: Int, x2: Int, x3: Int): Int = {
    val y1 = x1 + x2
    val y2 = y1 - x3
    if (y2 == 0) {
      return 9
    }
    return y2
  }
}
