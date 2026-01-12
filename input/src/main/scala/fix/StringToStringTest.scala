/*
rule = StringToString
 */
package fix

object StringToStringTest {
  def f1(s: String): String = s.toString
  def f2(s: java.lang.String): String = s.toString
}
