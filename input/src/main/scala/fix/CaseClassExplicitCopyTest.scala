/*
rule = CaseClassExplicitCopy
 */
package fix

object CaseClassExplicitCopyTest {
  case class A1(x: Int) {
    def copy(y: String): A1 = this // assert: CaseClassExplicitCopy
    def foo(): Int = x
  }

  abstract case class A2(x: Int) {
    def copy(y: String): A2 = this
  }

  class A3(x: Int) {
    def copy(y: String): A3 = this
  }
}
