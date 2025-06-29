/*
rule = DefImplicitParamToUsingParam
 */
package fix

import scala.language.implicitConversions

trait DefImplicitParamToUsingParamTest {
  protected def f1(x1: Int)(x2: String): Int
  def f2(x1: Int)(implicit x2: String): Int
  final def f3(x1: Int)(implicit x2: String): Int = x1 + x2.length
  def f4(implicit x1: Int): Int = x1

  implicit def f5(x1: Int)(x2: String): Int
  implicit def f6(x1: Int)(implicit x2: String): Int
  implicit def f7(x1: Int)(implicit x2: String): Int = x1 + x2.length
  implicit def f8(implicit x1: Int): Int = x1
}
