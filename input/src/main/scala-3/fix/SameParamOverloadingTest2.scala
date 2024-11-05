/*
rule = SameParamOverloading
 */
package fix

import scala.reflect.ClassTag

object SameParamOverloadingTest2 {

  abstract class A1 {
    def x1[A](a: A)(using ClassTag[A]): Int // assert: SameParamOverloading
    def x1[A](b: A): String // assert: SameParamOverloading
  }
}
