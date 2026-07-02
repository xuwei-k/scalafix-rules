/*
rule = UseGivenByExplicitName
 */
package fix

object UseGivenByExplicitNameTest {
  given a1: Int = 2
  val a2: Int = 3
  given a3[A]: List[A] = Nil
  given Int = 4

  def a4(using x1: Int): Int = {
    x1 - 9 // assert: UseGivenByExplicitName
  }

  class B1(using x1: Int) {
    def f: Int = x1 * 2 // assert: UseGivenByExplicitName
  }

  def f1: Int = UseGivenByExplicitNameTest.a1 // assert: UseGivenByExplicitName

  def f2: Int = UseGivenByExplicitNameTest.a2

  import UseGivenByExplicitNameTest.a1 // assert: UseGivenByExplicitName
  import UseGivenByExplicitNameTest.a1 as c1 // assert: UseGivenByExplicitName
  import UseGivenByExplicitNameTest.a2
  import UseGivenByExplicitNameTest.a3 // assert: UseGivenByExplicitName
}
