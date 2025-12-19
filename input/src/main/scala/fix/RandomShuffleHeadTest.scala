/*
rule = RandomShuffleHead
 */
package fix

import scala.util.Random

object RandomShuffleHeadTest {
  trait NotRandom {
    def shuffle[A](seq: Seq[A]): Seq[A]
  }

  def f1(xs: Seq[Int]): Int = Random.shuffle(xs).head // assert: RandomShuffleHead

  def f2[A](r: Random, xs: Seq[A]): A = r.shuffle(xs).head // assert: RandomShuffleHead

  def f3[A](r: NotRandom, xs: Seq[A]): A = r.shuffle(xs).head
}
