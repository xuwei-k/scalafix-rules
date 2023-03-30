/*
rule = OptionGetWarn
 */
package fix

object OptionGetWarnTest {
  def f1[A](a: Option[A]): A = a.get // assert: OptionGetWarn
  def f3: Option[String] = None

  f3.get // assert: OptionGetWarn

  trait MyClass[A] {
    def get: A
  }

  def f2[A](a: MyClass[A]): A = a.get
}
