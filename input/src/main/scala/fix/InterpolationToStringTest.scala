/*
rule = InterpolationToString
 */
package fix

object InterpolationToStringTest {
  trait A {
    def toString(a: Int): String
  }
  def x1(i: Int, a: A) = {
    s" ${i.toString} b ${i.toString} c"
    s" ${2.toString} b "
    f" ${i.toString} b "
    s" ${a.toString(i)} b c "
  }
}
