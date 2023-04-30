/*
rule = ReuseInstances
 */
package fix

object ReuseInstancesTest {
  def f1(a: Option[Int]): Option[Int] = a match {
    case Some(b) => Some(b)
    case None => Some(3)
  }

  def f2[A](a: Either[Int, A]): Either[Int, A] = a match {
    case Left(b) => Left(b)
    case Right(_) => Left(4)
  }

  def f3[A](a: Either[A, Int]): Either[A, Int] = a match {
    case Right(b) => Right(b)
    case Left(_) => Right(4)
  }

  def f4[A](a: List[Option[A]]): List[Option[A]] = a match {
    case List(Some(b1), None, Some(b2)) =>
      List(Some(b1), None, Some(b2))
    case List(None, None, None, Some(b3)) =>
      List(None, None, None, Some(b3))
    case _ =>
      Nil
  }
}
