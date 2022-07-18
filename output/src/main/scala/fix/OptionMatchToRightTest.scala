package fix

class OptionMatchToRightTest {
  def x1: Either[String, Int] = {
    Option(2).toRight("a")
  }

  def x2: Either[String, Int] = {
    Option(3).toRight("b")
  }

  def x3: Either[String, Int] = {
    Option(4) match {
      case Some(y) if y % 2 == 0 => Right(y)
      case None => Left("c")
    }
  }

  def x4: Either[String, Int] = {
    Option(5) match {
      case Some(y) => Right(y)
      case None => Left("d")
      case _ => Left("e")
    }
  }

  def x5: Either[String, Int] = {
    Option(6).toRight("f")
  }
}
