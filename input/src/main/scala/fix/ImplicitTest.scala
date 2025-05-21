/*
rule = Implicit
 */
package fix

import scala.annotation.implicitAmbiguous
import scala.annotation.implicitNotFound

trait ImplicitTest {
  implicit def f0: Int // assert: Implicit

  def f1[A](implicit x: A): A // assert: Implicit

  implicit val f2: Int = 2 // assert: Implicit

  implicit lazy val f3: Int = 3 // assert: Implicit

  implicit class A1(x: Int) // assert: Implicit

  implicit object A2 // assert: Implicit

  def f4: DummyImplicit = implicitly[DummyImplicit]

  @implicitNotFound("aaa")
  @implicitAmbiguous("bbb")
  trait B

  def g = "implicit"
}
