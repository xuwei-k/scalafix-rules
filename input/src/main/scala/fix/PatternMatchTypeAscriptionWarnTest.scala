/*
rule = PatternMatchTypeAscriptionWarn
 */
package fix

object PatternMatchTypeAscriptionWarnTest {
  val (a, b, c): (Int, String, Boolean) = (2, "a", false) // assert: PatternMatchTypeAscriptionWarn
}
