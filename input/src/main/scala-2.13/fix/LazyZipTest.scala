/*
rule = LazyZip
 */
package fix

trait LazyZipTest {
  def list1: List[Int]
  def list2: List[(String, Boolean)]

  list1.zip(list2).map { case (x1, x2) =>
    x2._1.take(x1)
  }

  list1.zip(list2).map { case (x1, (x2, x3)) =>
    (x1, x2, x3)
  }

  list1.zip(list2).foreach { x =>
    println(x)
  }

  list1.zip(list2).exists(_._2._2)

}
