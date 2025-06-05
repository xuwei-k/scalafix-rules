/*
rule = ImplicitImplicit
 */
package fix

object ImplicitImplicitTest {
  class A(implicit
    a: Int,
    implicit val b: String,
    c: Long,
    private implicit val d: Boolean
  )
}
