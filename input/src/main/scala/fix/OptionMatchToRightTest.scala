/*
rule = OptionMatchToRight
 */
package fix

class OptionMatchToRightTest {
  def x1: Either[String, Int] = {
    Option(2) match {
      case Some(x) => Right(x)
      case None => Left("a")
    }
  }

  def x2: Either[String, Int] = {
    Option(3) match {
      case Some(y) => Right(y)
      case _ => Left("b")
    }
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
    Option(6) match {
      case None => Left("f")
      case Some(x) => Right(x)
    }
  }
}
