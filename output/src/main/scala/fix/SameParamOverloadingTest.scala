package fix

import scala.reflect.ClassTag

object SameParamOverloadingTest {

  abstract class A1 {
    def x1[A: ClassTag](a: A): Int 
    def x1[A](b: A): Int 
  }

  trait A2 {
    def x1(a1: Int, a2: Int): Int
    def x1(a1: Int): Int
  }

}
