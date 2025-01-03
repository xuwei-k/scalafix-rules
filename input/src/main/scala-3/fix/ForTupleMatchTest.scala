/*
rule = ForTupleMatch
 */
package fix

trait ForTupleMatchTest {
  def x1: List[(Int, String)]
  def x2: List[(Int, String, Boolean)]

  def f0: List[(String, Int)] = for {
    a <- x2
    (b, c, _) = a
  } yield (c, b)

  def f1: List[(String, Int)] = for {
    a <- x1
    (b, c) = a
  } yield (c, b)

  def f2: List[((Int, String), Int)] = for {
    a <- x1
    (b, c) = a
  } yield (a, b)

  def f3: Unit = for {
    a <- x1
    (b, c) = a
  } {
    println(b)
  }

  def f4: Unit = for {
    a <- x1
    (b, c) = a
  } {
    println(a._2)
  }
}
