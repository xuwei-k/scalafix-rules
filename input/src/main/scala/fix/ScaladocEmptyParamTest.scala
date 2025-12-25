/*
rule = ScaladocEmptyParam
 */
package fix

/**
 * @param a1
 */
abstract class ScaladocEmptyParamTest(a1: Int) {

  /**
   * @param x1 aaa
   * @param x2
   * @return bbb
   */
  def f1(x1: Int, x2: Int): Int = x1 + x2

  /**
   * @param x1
   * @param x2 aaa
   */
  def f2(x1: Int, x2: Int): Int
}
