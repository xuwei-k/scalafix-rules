/*
rule = FinalObjectWarn
 */
package fix

class FinalObjectWarnTest {
  final object A1 // assert: FinalObjectWarn
  object A2
  final class A3
}
