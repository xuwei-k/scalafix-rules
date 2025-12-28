/*
rule = UnnecessaryPartitionMap
 */
package fix

import scala.reflect.ClassTag

trait UnnecessaryPartitionMapTest[A, B, C, D] {
  protected def f: A => Either[B, C]
  protected def f2: ((A, B)) => Either[C, D]

  def seq(x1: Seq[A]): (Seq[B], Seq[C]) = {
    val (y1, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
    val (_, y2) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
    val (z1, z2) = x1.partitionMap(f)
    (y1, y2)
  }

  def range(x1: Range, x2: Int => Either[B, C]): Seq[B] = {
    val (y, _) = x1.partitionMap(x2) // assert: UnnecessaryPartitionMap
    y
  }

  def list(x1: List[A]): List[B] = {
    val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
    y
  }

  def vector(x1: Vector[A]): Vector[B] = {
    val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
    y
  }

  def set(x1: Set[A]): Set[B] = {
    val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
    y
  }

  def map(x1: Map[A, B]): Iterable[C] = {
    val (y, _) = x1.partitionMap(f2) // assert: UnnecessaryPartitionMap
    y
  }

  def array(x1: Array[A])(implicit b: ClassTag[B], c: ClassTag[C]): Array[B] = {
    val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
    y
  }

  object mutable {
    def seq(x1: collection.mutable.Seq[A]): collection.mutable.Seq[B] = {
      val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
      y
    }

    def set(x1: collection.mutable.Set[A]): collection.mutable.Set[B] = {
      val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
      y
    }

    def map(x1: collection.mutable.Map[A, B]): Iterable[C] = {
      val (y, _) = x1.partitionMap(f2) // assert: UnnecessaryPartitionMap
      y
    }
  }

  object immutable {
    def seq(x1: collection.immutable.Seq[A]): collection.immutable.Seq[B] = {
      val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
      y
    }

    def set(x1: collection.immutable.Set[A]): collection.immutable.Set[B] = {
      val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
      y
    }

    def map(x1: collection.immutable.Map[A, B]): Iterable[C] = {
      val (y, _) = x1.partitionMap(f2) // assert: UnnecessaryPartitionMap
      y
    }
  }

  object ScalaCollection {
    def seq(x1: collection.Seq[A]): collection.Seq[B] = {
      val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
      y
    }

    def set(x1: collection.Set[A]): collection.Set[B] = {
      val (y, _) = x1.partitionMap(f) // assert: UnnecessaryPartitionMap
      y
    }

    def map(x1: collection.Map[A, B]): Iterable[C] = {
      val (y, _) = x1.partitionMap(f2) // assert: UnnecessaryPartitionMap
      y
    }
  }
}
