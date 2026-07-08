/*
rule = BigDecimalFromDoubleLiteral
 */
package fix

import java.math.MathContext

object BigDecimalFromDoubleLiteralTest {
  def f1(d1: Double): List[BigDecimal] = List(
    BigDecimal(1.5), // assert: BigDecimalFromDoubleLiteral
    BigDecimal(1.5, MathContext.UNLIMITED), // assert: BigDecimalFromDoubleLiteral
    BigDecimal.apply(1.5), // assert: BigDecimalFromDoubleLiteral
    BigDecimal.apply(1.5, MathContext.UNLIMITED), // assert: BigDecimalFromDoubleLiteral
    BigDecimal.decimal(1.5), // assert: BigDecimalFromDoubleLiteral
    BigDecimal.decimal(1.5, MathContext.UNLIMITED), // assert: BigDecimalFromDoubleLiteral
    BigDecimal("1.5"),
    BigDecimal(d1),
  )
}
