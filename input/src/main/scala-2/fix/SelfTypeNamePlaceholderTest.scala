/*
rule = SelfTypeNamePlaceholder
 */
package fix

class SelfTypeNamePlaceholderTest {
  trait B

  trait A1 { self => }
  trait A2 { self: B => }
  trait A3 { _: B => } // assert: SelfTypeNamePlaceholder
}
