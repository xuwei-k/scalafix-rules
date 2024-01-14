package fix

object PartialFunctionCondOptTest {
  def x1(a: String): Option[Int] = PartialFunction.condOpt(a) {
    case "1" => 4
    case "2" => 10
    case "3" => 12340
  }

  def x2(a: String, b: Int): Option[Int] = a match {
    case "1" => Some(3)
    case "2" =>
      Some(
        (PartialFunction.condOpt(b) {
          case 1 => 100
          case 2 => 200
        }).getOrElse(-1)
      )
    case _ =>
      None
  }
}
