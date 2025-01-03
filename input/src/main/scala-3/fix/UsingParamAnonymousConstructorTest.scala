/*
rule = UsingParamAnonymousConstructor
 */
package fix

trait UsingParamAnonymousConstructorTest {
  class A1(using x1: Int)

  class A2(using x1: Int) {
    def f: Int = x1
  }

  trait B1(using x1: Int)

  trait B2(using x1: Int) {
    def f: Int = x1
  }
}
