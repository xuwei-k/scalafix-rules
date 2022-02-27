/*
rule = UnusedTypeParams
 */
package fix

import scala.reflect.ClassTag

object UnusedTypeParamsTest {
  def a1[A](a: Int) = a + 1 // assert: UnusedTypeParams

  def a2[A](a: Int)(implicit c: ClassTag[A]) = a + 1

  def a3[A: ClassTag](a: Int) = a + 1
}
