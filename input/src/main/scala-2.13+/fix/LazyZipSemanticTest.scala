/*
rule = LazyZipSemantic
 */
package fix

import scala.collection.mutable

trait LazyZipSemanticTest {
  def list1: List[Int]
  def list2: List[(String, Boolean)]
  def f: ((Int, (String, Boolean))) => Byte

  list1.zip(list2).map { case (x1, x2) =>
    x2._1.take(x1)
  }

  list1.zip(list2).map { case (x1, _) =>
    x1
  }

  list1.zip(list2).map { case (x1, (x2, x3)) =>
    (x1, x2, x3)
  }

  list1.zip(list2).foreach { x =>
    println(x)
  }

  list1.zip(list2).exists(_._2._2)

  list1.zip(list2).map(f)

  def mutableSeq[A, B](x1: mutable.Seq[A], x2: mutable.Seq[B]): mutable.Seq[Int] =
    x1.zip(x2).map { case (a, b) => 3 }

  def vector1[A, B](x1: Vector[A], x2: Vector[B]): Vector[Int] =
    x1.zip(x2).map { case (a, b) => 3 }

  def seq1[A, B](x1: Seq[A], x2: Seq[B]): Seq[Int] =
    x1.zip(x2).map { case (a, b) => 3 }

  def array1[A, B](x1: Array[A], x2: Array[B]): Array[Int] =
    x1.zip(x2).map { case (a, b) => 3 }
}
