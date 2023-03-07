/*
rule = DesugarForYield
 */
package fix

class DesugarForYieldTest {
  def f1 = for {
    a1 <- List(1, 2)
    a2 <- List("x", "y")
    a3 <- List(true)
  } yield (a1, a2, a3)
}
