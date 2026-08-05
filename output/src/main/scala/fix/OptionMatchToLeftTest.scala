package fix

class OptionMatchToLeftTest {
  def x1: Either[Int, String] = {
    Option(2).toLeft("a")
  }

  def x2: Either[Int, String] = {
    Option(3).toLeft("b")
  }

  def x3: Either[Int, String] = {
    Option(4) match {
      case Some(y) if y % 2 == 0 => Left(y)
      case None => Right("c")
    }
  }

  def x4: Either[Int, String] = {
    Option(5) match {
      case Some(y) => Left(y)
      case None => Right("d")
      case _ => Right("e")
    }
  }

  def x5: Either[Int, String] = {
    Option(6).toLeft("f")
  }
}
