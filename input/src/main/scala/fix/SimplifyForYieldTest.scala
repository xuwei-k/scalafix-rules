package fix

/*
rule = SimplifyForYield
 */
object SimplifyForYieldTest {
  def x = for {
    y <- Option(2)
  } yield y
}
