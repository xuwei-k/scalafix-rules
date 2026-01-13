/*
rule = SIP62Bug24673
 */
package fix

trait SIP62Bug24673Test {
  def foo: Int

  def f1: Option[Int] = for {
    a <- Option(2)
  } yield a

  def f2: Option[Int] = for {
    a <- Option(2)
    b = foo // assert: SIP62Bug24673
  } yield a

  def f3: Option[Int] = for {
    a1 <- Option(2)
    a2 <- Option(3)
    b = {
      println(a2)
      foo
    }
  } yield a1

  def f4: Option[Int] = for {
    a1 <- Option(2)
    a2 <- Option(a1 + 3)
    b = foo // assert: SIP62Bug24673
  } yield a2

  def f5: Option[Int] = for {
    a1 <- Option(2)
    a2 <- Option(3)
    b = foo
  } yield (a1 + a2)
}
