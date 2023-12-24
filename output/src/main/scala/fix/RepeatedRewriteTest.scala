package fix

class RepeatedRewriteTest {
  val xs = 1 to 10

  def f1: List[Int] = List(xs *)
  def f2: Vector[Int] = Vector((xs ++ xs) *)
}
