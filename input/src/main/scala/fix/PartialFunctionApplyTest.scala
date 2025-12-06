/*
rule = PartialFunctionApply
 */
package fix

object PartialFunctionApplyTest {
  def f1(p: PartialFunction[Int, Int]): Int = p(2) // assert: PartialFunctionApply

  def f2(p: Function[Int, Int]): Int = {
    p(2)
    p.apply(3)
    Map.empty[Int, Int].apply(9)
  }
}
