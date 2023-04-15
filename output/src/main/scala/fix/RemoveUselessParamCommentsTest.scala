package fix

/**
  * @param a3 aaaaaaaaaa
  */
abstract class RemoveUselessParamCommentsTest(
  a1: Int,
  a2: Int,
  a3: Int,
  a4: Int,
) {

  /**
    * @param b2 bbbbbbbb
    */
  def x1(
    b1: String,
    b2: String,
    b3: String,
  ): String

  /**
    * @param c3 ccccccc
    */
  def x2(
    c1: String,
    c2: String,
    c3: String,
  ): String = ""
}
