package fix

trait WithLeftWithRightTest {
  def x1: Either[String, Int] = Right(2).withLeft[String]
  def x2: Either[String, Int] = Left("a").withRight[Int]
}
