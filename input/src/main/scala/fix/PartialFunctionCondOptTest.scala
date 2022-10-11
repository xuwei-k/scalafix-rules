/*
rule = PartialFunctionCondOpt
 */
package fix

object PartialFunctionCondOptTest {
  def x1(a: String): Option[Int] = a match {
    case "1" => Some(4)
    case "2" => Some(10)
    case "3" => Some(12340)
    case _ => None
  }
}
