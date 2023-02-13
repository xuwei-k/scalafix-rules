/*
rule = RedundantCaseClassVal
 */
package fix

object RedundantCaseClassValTest {

  trait X2 {
    def a4: Int
  }

  case class X1(
    a0: Int,
    val a1: Int, // assert: RedundantCaseClassVal
    private val a2: Int,
    protected val a3: Int,
    override val a4: Int,
    final val a5: Int,
    var a6: Int,
  )(
    val a7: Int
  ) extends X2
}
