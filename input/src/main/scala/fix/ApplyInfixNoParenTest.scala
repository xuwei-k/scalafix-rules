/*
rule = ApplyInfixNoParen
 */
package fix

trait ApplyInfixNoParenTest {
  def f1: Boolean = false == true && false // assert: ApplyInfixNoParen
  def f2: Boolean = (false == true) && false // これはOK
  def f3: Boolean = false == (true && false) // これもOK
}
