/*
rule = DubiousLiteral
 */
package fix

// format: off
object DubiousLiteralTest {
  def f0: Int = -2
  def f1: Int = -2.abs // assert: DubiousLiteral
  def f2: Int = - 2.abs // assert: DubiousLiteral
  def f3: Int = - 2 // assert: DubiousLiteral
  def f4: Int = 42
    -2.abs // assert: DubiousLiteral
  def f5: Int = 42
    - 2.abs
  def f6: Int = (-2).abs
  def f7: Double = -3.14
  def f8: Long = -2L.abs // https://github.com/scala/scala3/issues/26564
  def f9: Double = -3.14.abs
}
// format: on
