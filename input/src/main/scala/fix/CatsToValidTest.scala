/*
rule = CatsToValid
 */
package fix

import cats.data.Validated
import cats.data.ValidatedNec
import cats.data.ValidatedNel
import cats.syntax.all.*

class CatsToValidTest {
  def f1[A](x: Option[A]): Validated[String, A] = x match {
    case Some(value) => Validated.valid(value)
    case None => Validated.invalid("error 1")
  }

  def f2[A](x: Option[A]): ValidatedNec[String, A] = x match {
    case Some(value) => Validated.valid(value)
    case None => Validated.invalidNec("error 2")
  }

  def f3[A](x: Option[A]): ValidatedNel[String, A] = x match {
    case Some(value) => Validated.valid(value)
    case None => Validated.invalidNel("error 3")
  }

  def f4[A](x: Option[A]): Validated[String, A] = x match {
    case Some(value) => Validated.valid(value)
    case _ => Validated.invalid("error 4")
  }

  def f5[A](x: Option[A]): Validated[String, A] = x match {
    case None => Validated.invalid("error 5")
    case Some(value) => Validated.valid(value)
  }

  def f6[A](x: Option[A], c: Boolean): Validated[String, A] = x match {
    case Some(value) if c => Validated.valid(value)
    case _ => Validated.invalid("error 6")
  }

  def f7[A](x: Option[A]): Validated[String, A] = x match {
    case Some(value) => value.valid
    case _ => "error 7".invalid
  }
}
