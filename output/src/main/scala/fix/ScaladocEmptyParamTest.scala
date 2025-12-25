package fix

/**
 */
abstract class ScaladocEmptyParamTest(a1: Int) {

  /**
   * @param x1 aaa
   * @return bbb
   */
  def f1(x1: Int, x2: Int): Int = x1 + x2

  /**
   * @param x2 aaa
   */
  def f2(x1: Int, x2: Int): Int
}
