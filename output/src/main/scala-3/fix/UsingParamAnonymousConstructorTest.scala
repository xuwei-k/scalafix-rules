package fix

trait UsingParamAnonymousConstructorTest {
  class A1(using  Int)

  class A2(using x1: Int) {
    def f: Int = x1
  }

  class A3(using val x1: Int)

  class A4(using var x1: Int)

  class A5(using @annotation.nowarn x1: Int)

  class A6(using x1: Int = 3)

  /**
   * @param a2 aaa
   */
  class A7(a1: Int)(using a2: Int, a3: Int)

  /**
   * @param a1 aaa
   */
  class A8(a1: Int)(using  Int,  Int)

  trait B1(using  Int)

  trait B2(using x1: Int) {
    def f: Int = x1
  }
}
