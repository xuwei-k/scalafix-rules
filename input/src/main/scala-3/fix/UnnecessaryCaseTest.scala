/*
rule = UnnecessaryCase
 */
package fix

class UnnecessaryCaseTest {
  def f1: Int => Int = { case a => a }
  def f2: PartialFunction[Int, Int] = { case a => a }
  private val z = 3
  private val C = 7
  def f3: Int => Int = { case `z` => 2 }
  def f4: PartialFunction[Int, Int] = { case `z` => 3 }
  def f5: Int => Int = { case C => 2 }
  def f6: PartialFunction[Int, Int] = { case C => 3 }

  List(4).foreach { case a => a }
  List(5).map { case a => a }
  List(6).collect { case a => a }
}
