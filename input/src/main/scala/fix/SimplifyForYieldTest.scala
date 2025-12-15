/*
rule = SimplifyForYield
 */
package fix

object SimplifyForYieldTest {
  def x = for {
    y <- Option(2)
  } yield y

  def x2 = (for {
    y <- 1 to 10
  } yield y).head
}
