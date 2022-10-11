package fix

object PartialFunctionCondOptTest {
  def x1(a: String): Option[Int] = PartialFunction.condOpt(a) {
  case "1" => 4
  case "2" => 10
  case "3" => 12340
}
}
