/** pkg */
package foo

/** AAA */
class A(
  /** xxx */
  val x: Int
) {

  /** secondary constructor */
  def this(s: String) = {
    this(3)
  }

  /** yyy */
  def y(n: Int): Int = {
    val m = {
      n match {
        /* ddd */
        case 1 =>
          3
        /** ccc */
        case _ =>
          4
      }
    }

    /** zzz */
    for {
      /** sss */
      s <- 1 to 10
    } yield s

    m + n
  }
}
