/*
rule = RemoveUselessParamComments
 */
package fix

/**
  * @param a1 a1
  * @param a2
  * @param a3 aaaaaaaaaa
  * @param a4 A4
  */
abstract class RemoveUselessParamCommentsTest(
  a1: Int,
  a2: Int,
  a3: Int,
  a4: Int,
) {

  /**
    * @param b1 b1
    * @param b2 bbbbbbbb
    * @param b3 B3
    */
  def x1(
    b1: String,
    b2: String,
    b3: String,
  ): String

  /**
    * @param c1 C1
    * @param c2
    * @param c3 ccccccc
    */
  def x2(
    c1: String,
    c2: String,
    c3: String,
  ): String = ""
}
