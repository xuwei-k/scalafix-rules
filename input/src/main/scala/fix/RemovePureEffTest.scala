/*
rule = RemovePureEff
 */
package fix

import org.atnos.eff.Eff
import org.atnos.eff.syntax.eff.*

class RemovePureEffTest {

  def x[R]: Eff[R, (Int, Int, Eff[R, Int], Int, Int, String)] = for {
    a1 <- 1.pureEff[R]
    a2 <- 2.pureEff[R]
    a3 = 3.pureEff[R]
    a4 <- 4.pureEff[R]
    a5 <- Eff.pure(5)
    a6 <- Eff.pure[R, String]("aaa")
  } yield (a1, a2, a3, a4, a5, a6)

}
