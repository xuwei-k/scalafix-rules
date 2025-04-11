/*
rule = AutoEtaExpansion
 */
package fix

trait AutoEtaExpansionTest {
  def f(a: Int): Int

  def x1: Int => Int = f _ // assert: AutoEtaExpansion
  def x2: Int => Int = f(_)

  def x3: Option[Int] = Option(2).map(f _) // assert: AutoEtaExpansion
  def x4: Option[Int] = Option(4).map(f(_))
}
