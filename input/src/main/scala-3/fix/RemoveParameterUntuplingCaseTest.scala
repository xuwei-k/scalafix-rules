/*
rule = RemoveParameterUntuplingCase
 */
package fix

trait RemoveParameterUntuplingCaseTest {
  def xs: List[(Int, Int)]

  final val C: Int = 3

  val D: PartialFunction[Int, Int] = { case x => x }

  xs.map { case (a, b) => a + b }

  xs.map { case ((a, b)) => (b, a) }

  xs.map { case (a, 2) => a }

  xs.map { case (a, C) => a }

  xs.map { case (D(a), b) => a + b }
}
