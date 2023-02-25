/*
rule = SizeToLength
 */
package fix

object SizeToLengthTest {
  def f1(x: String): Int = x.size
  def f2[A](x: Seq[A]): Int = x.size
  def f3[A](x: Array[A]): Int = x.size
}
