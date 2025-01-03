package fix

trait UsingParamAnonymousConstructorTest {
  class A1(using  Int)

  class A2(using x1: Int) {
    def f: Int = x1
  }

  trait B1(using  Int)

  trait B2(using x1: Int) {
    def f: Int = x1
  }
}
