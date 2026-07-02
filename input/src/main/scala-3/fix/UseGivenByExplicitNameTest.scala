/*
rule = UseGivenByExplicitName
 */
package fix

object UseGivenByExplicitNameTest {
  given a1: Int = 2
  val a2: Int = 2

  def f1: Int = UseGivenByExplicitNameTest.a1 // assert: UseGivenByExplicitName

  def f2: Int = UseGivenByExplicitNameTest.a2

  import UseGivenByExplicitNameTest.a1 // assert: UseGivenByExplicitName
  import UseGivenByExplicitNameTest.a1 as c1 // assert: UseGivenByExplicitName
  import UseGivenByExplicitNameTest.a2
}
