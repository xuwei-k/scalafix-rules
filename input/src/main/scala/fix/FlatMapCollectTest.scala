/*
rule = FlatMapCollect
 */
package fix

trait FlatMapCollectTest {

  def f1(x: Option[Int]): Option[Int] =
    x.flatMap {
      case 1 => Some(10)
      case 2 => Some(20)
      case _ => None
    }

  def f2(x: Seq[String]): Seq[Int] =
    x.flatMap {
      case "a" => Some(1)
      case "b" => Some(2)
      case y => None
    }

  def f3(x: Option[Int], y: Option[Int]): Option[Int] =
    x.flatMap {
      case 9 => Some(1)
      case 8 => y
      case _ => None
    }
}
