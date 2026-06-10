/*
rule = PatternMatchTypeAscriptionRemove
 */
package fix

object PatternMatchTypeAscriptionRemoveTest {
  val (a, b, c): (Int, String, Boolean) = (2, "a", false)
}
