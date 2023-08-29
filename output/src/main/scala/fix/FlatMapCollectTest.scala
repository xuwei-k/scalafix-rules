package fix

trait FlatMapCollectTest {

  def f1(x: Option[Int]): Option[Int] =
    x.collect {
      case 1 => 10
      case 2 => 20
    }

  def f2(x: Seq[String]): Seq[Int] =
    x.collect {
      case "a" => 1
      case "b" => 2
    }

  def f3(x: Option[Int], y: Option[Int]): Option[Int] =
    x.flatMap {
      case 9 => Some(1)
      case 8 => y
      case _ => None
    }
}
