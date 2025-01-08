package fix

import cats.data.Validated
import cats.data.ValidatedNec
import cats.data.ValidatedNel
import cats.syntax.all.*

class CatsToValidTest {
  def f1[A](x: Option[A]): Validated[String, A] = x.toValid("error 1")

  def f2[A](x: Option[A]): ValidatedNec[String, A] = x.toValidNec("error 2")

  def f3[A](x: Option[A]): ValidatedNel[String, A] = x.toValidNel("error 3")

  def f4[A](x: Option[A]): Validated[String, A] = x.toValid("error 4")

  def f5[A](x: Option[A]): Validated[String, A] = x.toValid("error 5")

  def f6[A](x: Option[A], c: Boolean): Validated[String, A] = x match {
    case Some(value) if c => Validated.valid(value)
    case _ => Validated.invalid("error 6")
  }

  def f7[A](x: Option[A]): Validated[String, A] = x.toValid("error 7")
}
