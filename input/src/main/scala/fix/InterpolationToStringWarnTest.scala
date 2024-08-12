/*
rule = InterpolationToStringWarn
 */
package fix

object InterpolationToStringWarnTest {
  trait A {
    def toString(a: Int): String
  }
  def x1(i: Int, a: A) = {
    s" ${i.toString} b ${i.toString} c" // assert: InterpolationToStringWarn
    s" ${2.toString} b " // assert: InterpolationToStringWarn
    s" ${i.toString()} b " // assert: InterpolationToStringWarn
    f" ${i.toString} b "
    s" ${a.toString(i)} b c "
  }
}
