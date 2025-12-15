package fix

import org.atnos.eff.Eff
import org.atnos.eff.syntax.eff.*

class RemovePureEffTest {

  def x[R]: Eff[R, (Int, Int, Eff[R, Int], Int, Int, String)] = for {
    a1 <- 1.pureEff[R]
    a2 = 2
    a3 = 3.pureEff[R]
    a4 = 4
    a5 = 5
    a6 = "aaa"
  } yield (a1, a2, a3, a4, a5, a6)

}
