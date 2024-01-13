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

  def x2(a: String, b: Int): Option[Int] = a match {
    case "1" => Some(3)
    case "2" =>
      Some(
        (b match {
          case 1 => Some(100)
          case 2 => Some(200)
          case _ => None
        }).getOrElse(-1)
      )
    case _ =>
      None
  }
}
