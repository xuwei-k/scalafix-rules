package fix

class UnnecessaryCaseTest {
  def f1: Int => Int = {  a => a }
  def f2: PartialFunction[Int, Int] = {  a => a }
  private val z = 3
  private val C = 7
  def f3: Int => Int = { case `z` => 2 }
  def f4: PartialFunction[Int, Int] = { case `z` => 3 }
  def f5: Int => Int = { case C => 2 }
  def f6: PartialFunction[Int, Int] = { case C => 3 }

  List(4).foreach {  a => a }
  List(5).map {  a => a }
  List(6).collect {  a => a }
  List(7).collect {  _ => 7 }
}
