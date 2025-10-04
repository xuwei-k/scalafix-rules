/*
rule = RedundantCaseClassVal
 */
package fix

object RedundantCaseClassValEnumCaseTest {
  enum A {
    case B1(val x: Int) // assert: RedundantCaseClassVal
    case B2(
      x: Int,
      val y: Int // assert: RedundantCaseClassVal
    )
    case B3(x: Int)
    case B4(x1: Int)(implicit val x2: Int)
    case B5(private val x: Int)
  }
}
