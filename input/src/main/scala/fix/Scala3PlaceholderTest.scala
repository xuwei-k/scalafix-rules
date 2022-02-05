package fix

/*
rule = Scala3Placeholder
 */
class Scala3PlaceholderTest {
  def a1: Class[_] = classOf[String] // assert: Scala3Placeholder
}
