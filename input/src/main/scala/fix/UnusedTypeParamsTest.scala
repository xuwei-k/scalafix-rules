/*
rule = UnusedTypeParams
 */
package fix

import scala.annotation.unused
import scala.reflect.ClassTag

object UnusedTypeParamsTest {
  def a1[A](a: Int) = a + 1 // assert: UnusedTypeParams

  def a2[A](a: Int)(implicit c: ClassTag[A]) = a + 1

  def a3[A: ClassTag](a: Int) = a + 1

  def a4[@unused A](a: Int) = a

  def a5[@annotation.unused A](a: Int) = a

  def a6[@scala.annotation.unused A](a: Int) = a

  def a7[@ _root_.scala.annotation.unused A](a: Int) = a

  trait A1[F[_]] {
    def x[B]: F[B]
  }

  class A2 extends A1[List] {
    override def x[B] = Nil
  }
}
