/*
rule = ScaladocEmptyReturn
 */
package fix

trait ScaladocEmptyReturnTest {

  /**
   * @param x1 a
   * @return
   */
  def f1(x1: Int): Int

  /**
   * @param x1
   * @return b
   */
  def f2(x1: Int): Int

  /**
   * @param x1
   * @return
   *   b
   */
  def f3(x1: Int): Int
}
