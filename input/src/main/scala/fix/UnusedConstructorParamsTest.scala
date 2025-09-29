/*
rule = UnusedConstructorParams
 */
package fix

import scala.annotation.unused

object UnusedConstructorParamsTest {
  class A1(val x: Int)

  class A2(x: Int) // assert: UnusedConstructorParams

  class A3(`type`: Int) extends A1(`type`)

  class A4(var x: Int)

  class A5(val x: Int)(implicit x2: String)

  class A6(@unused x: Int)

  class A7(@annotation.unused x: Int)

  class A8(@scala.annotation.unused x: Int)

  class A9(@ _root_.scala.annotation.unused x: Int)

  class A10(
    @unused x1: Int,
    x2: Int, // assert: UnusedConstructorParams
    @unused x3: Int,
  )
}
