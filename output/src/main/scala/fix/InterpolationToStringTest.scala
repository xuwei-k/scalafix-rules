package fix

object InterpolationToStringTest {
  trait A {
    def toString(a: Int): String
  }
  def x1(i: Int, a: A) = {
    s" ${i} b ${i} c"
    s" ${2} b "
    f" ${i.toString} b "
    s" ${a.toString(i)} b c "
  }
}
