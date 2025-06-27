/*
rule = UsingParamAnonymous
 */
package fix

trait UsingParamAnonymousTest {
  def f1(using a1: Int, a2: Int): Int = List(a1, 2).sum
  def f2(using a1: Int, a2: String): Int = summon[Int] + summon[String].size
  def f3(using @annotation.unused a1: Int): Int = summon[Int]
}
