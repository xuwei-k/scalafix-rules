package fix

trait PartitionFilterTest[A] {
  protected def f: A => Boolean
  protected def f2: ((A, A)) => Boolean

  def seq(x1: Seq[A]): Seq[A] = {
    val y1 = x1.filter(f)
    val y2 = x1.filterNot(f)
    val (z1, z2) = x1.partition(f)
    y1 ++ y2
  }

  def range(x1: Range, x2: Int => Boolean): Seq[Int] = {
    val y = x1.filter(x2)
    y
  }

  def list(x1: List[A]): List[A] = {
    val y = x1.filter(f)
    y
  }

  def vector(x1: Vector[A]): Vector[A] = {
    val y = x1.filter(f)
    y
  }

  def set(x1: Set[A]): Set[A] = {
    val y = x1.filter(f)
    y
  }

  def map(x1: Map[A, A]): Map[A, A] = {
    val y = x1.filter(f2)
    y
  }

  def array(x1: Array[A]): Array[A] = {
    val y = x1.filter(f)
    y
  }

  object mutable {
    def seq(x1: collection.mutable.Seq[A]): collection.mutable.Seq[A] = {
      val y = x1.filter(f)
      y
    }

    def set(x1: collection.mutable.Set[A]): collection.mutable.Set[A] = {
      val y = x1.filter(f)
      y
    }

    def map(x1: collection.mutable.Map[A, A]): collection.mutable.Map[A, A] = {
      val y = x1.filter(f2)
      y
    }
  }

  object immutable {
    def seq(x1: collection.immutable.Seq[A]): collection.immutable.Seq[A] = {
      val y = x1.filter(f)
      y
    }

    def set(x1: collection.immutable.Set[A]): collection.immutable.Set[A] = {
      val y = x1.filter(f)
      y
    }

    def map(x1: collection.immutable.Map[A, A]): collection.immutable.Map[A, A] = {
      val y = x1.filter(f2)
      y
    }
  }

  object ScalaCollection {
    def seq(x1: collection.Seq[A]): collection.Seq[A] = {
      val y = x1.filter(f)
      y
    }

    def set(x1: collection.Set[A]): collection.Set[A] = {
      val y = x1.filter(f)
      y
    }

    def map(x1: collection.Map[A, A]): collection.Map[A, A] = {
      val y = x1.filter(f2)
      y
    }
  }
}
