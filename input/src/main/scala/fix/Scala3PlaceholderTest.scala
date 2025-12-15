/*
rule = Scala3Placeholder
 */
package fix

class Scala3PlaceholderTest {
  def a1: Class[_] = classOf[String] // assert: Scala3Placeholder
}
