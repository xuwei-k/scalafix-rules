/*
rule = ImplicitClassOnlyDef
 */
package fix

object ImplicitClassOnlyDefTest {
  implicit class A(x: Int) {
    import scala.util.Random // assert: ImplicitClassOnlyDef

    val x1 = 2 // assert: ImplicitClassOnlyDef

    var x2 = 2 // assert: ImplicitClassOnlyDef

    type F = Long // assert: ImplicitClassOnlyDef

    def f: Random = Random

    def g(a1: Int): Int = {
      val a2 = 2
      a1 + a2 + x
    }
  }

  class B {
    val x1 = 2
  }
}
