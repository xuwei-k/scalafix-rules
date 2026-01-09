/*
rule = FindExists
 */
package fix

object FindExistsTest {
  def seq1[A](x: Seq[A], f: A => Boolean): Seq[Boolean] = Seq(
    x.find(f).isEmpty,
    x.find(f).nonEmpty,
    x.find(f).isDefined,
  )

  def array1[A](x: Array[A], f: A => Boolean): Boolean =
    x.find(f).isDefined

  def list1[A](x: List[A], f: A => Boolean): Boolean =
    x.find(f).isDefined

  def vector1[A](x: Vector[A], f: A => Boolean): Boolean =
    x.find(f).isDefined

  def set1[A](x: Set[A], f: A => Boolean): Boolean =
    x.find(f).isDefined

  def map1[A, B](x: Map[A, B], f: ((A, B)) => Boolean): Boolean =
    x.find(f).isDefined

  def notScala1[A](x: MyClass[A], f: A => Boolean): Boolean =
    x.find(f).isDefined

  trait MyClass[A] {
    def find(f: A => Boolean): Option[A]
  }
}
