/*
rule = OptionMapFlatMap
 */
package fix

sealed trait OptionMapFlatMapTest {
  def f1[A](o: Option[A]): Option[(Int, A)] = o match { // assert: OptionMapFlatMap
    case Some(a) =>
      Option((2, a))
    case None =>
      None
  }

  def f2[A](o: Option[A]): Option[(Int, A)] = o match { // assert: OptionMapFlatMap
    case None =>
      None
    case Some(a) =>
      Option((2, a))
  }

  def f3[A](o: Option[A]): Option[(Int, A)] = o match { // assert: OptionMapFlatMap
    case Some(a) =>
      Option((2, a))
    case _ =>
      None
  }

  def f4[A](o: Option[A]): Option[(Int, A)] = o match {
    case Some(a) if a.## % 2 == 0 =>
      Option((2, a))
    case _ =>
      None
  }

  def g1[A](xs: List[Option[A]]): List[Option[(Int, A)]] = xs.collect { // assert: OptionMapFlatMap
    case Some(a) =>
      Option((2, a))
    case None =>
      None
  }

  def g2[A](xs: List[Option[A]]): List[Option[(Int, A)]] = xs.collect { // assert: OptionMapFlatMap
    case None =>
      None
    case Some(a) =>
      Option((2, a))
  }
}
