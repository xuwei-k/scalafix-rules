package fix

object GroupMapTest {
  def f[A, B](xs: List[(A, B)]): Map[A, List[B]] =
    xs.groupMap(_._1)(x => x._2)
}
