/*
rule = InterpolationToString
 */
package fix

object InterpolationToStringTest {
  class A {
    def toString(a: Int): String
  }
  def x1(i: Int, a: A) = {
    s" ${i.toString} b ${i.toString} c"
    s" ${2.toString} b "
    s" ${a.toString(x)} b c "
  }
}
