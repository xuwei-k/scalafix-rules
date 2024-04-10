/*
rule = ScalaBug4940
 */
package fix

class ScalaBug4940Test {
  def f1(x: List[Int]) = x.collect { case n => n }

  def f2(x: List[Int]) = x.collect { // assert: ScalaBug4940
    _ match {
      case n => n
    }
  }

  def f3(x: List[Int]) = x.collect { a => // assert: ScalaBug4940
    a match {
      case n => n
    }
  }
}
