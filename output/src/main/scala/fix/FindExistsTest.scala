package fix

object FindExistsTest {
  def seq1[A](x: Seq[A], f: A => Boolean): Seq[Boolean] = Seq(
    x.find(f).isEmpty,
    x.exists(f),
    x.exists(f),
  )

  def array1[A](x: Array[A], f: A => Boolean): Boolean =
    x.exists(f)

  def list1[A](x: List[A], f: A => Boolean): Boolean =
    x.exists(f)

  def vector1[A](x: Vector[A], f: A => Boolean): Boolean =
    x.exists(f)

  def set1[A](x: Set[A], f: A => Boolean): Boolean =
    x.exists(f)

  def map1[A, B](x: Map[A, B], f: ((A, B)) => Boolean): Boolean =
    x.exists(f)

  def notScala1[A](x: MyClass[A], f: A => Boolean): Boolean =
    x.find(f).isDefined

  trait MyClass[A] {
    def find(f: A => Boolean): Option[A]
  }
}
