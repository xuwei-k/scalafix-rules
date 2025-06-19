/*
rule = LeakingImplicitClassValAll
 */
package fix

object LeakingImplicitClassValAllTest {
  implicit class A1(x: Int)

  implicit class A2(private val x: Int)

  implicit class A3(private val x: Int) extends AnyVal

  implicit class A4(private[this] val x: Int)

  implicit class A5(private[fix] val x: Int) extends AnyVal // assert: LeakingImplicitClassValAll

  implicit class A6(private[fix] val x: Int) // assert: LeakingImplicitClassValAll

  implicit class A7(protected val x: Int) extends AnyVal // assert: LeakingImplicitClassValAll

  implicit class A8(protected[fix] val x: Int) // assert: LeakingImplicitClassValAll

  implicit class A9(protected val x: Int) // assert: LeakingImplicitClassValAll

  class A10(x: Int)
}
