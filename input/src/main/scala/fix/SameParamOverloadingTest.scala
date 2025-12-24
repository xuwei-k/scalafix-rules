/*
rule = SameParamOverloading
 */
package fix

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

object SameParamOverloadingTest {

  abstract class A1 {
    def x1[A: ClassTag](a: A): Int // assert: SameParamOverloading
    def x1[A](b: A): String // assert: SameParamOverloading

    def x2[A: ClassTag](a: Either[Int, A]): Int // assert: SameParamOverloading
    def x2(b: Either[Int, Int]): String // assert: SameParamOverloading
    def x2(c: Boolean Either Long)(implicit e: ExecutionContext): Boolean // assert: SameParamOverloading

    def func[A: ClassTag](a: Int => A): Int // assert: SameParamOverloading
    def func(b: String => Boolean): String // assert: SameParamOverloading
    def func(c: (String, Long) => Boolean): String

    def tuple[A: ClassTag](a: (Int, A)): Int // assert: SameParamOverloading
    def tuple(b: (Long, Boolean)): String // assert: SameParamOverloading
    def tuple(c: (Int, Char, Long)): String
  }

  abstract class A3 extends A1 {
    override def x1[A: ClassTag](a: A): Int = 2
    override def x1[A](b: A): String = "jjj"
  }

  trait A2 {
    def x1(a1: Int, a2: Int): Int
    def x1(a1: Int): Int
  }

}
