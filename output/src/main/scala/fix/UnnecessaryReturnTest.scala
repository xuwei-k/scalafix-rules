package fix

object UnnecessaryReturnTest {
  def f1(x1: Int, x2: Int, x3: Int): Int = {
    val y1 = x1 + x2
    val y2 = y1 - x3
    if (y2 == 0) {
      return 9
    }
    y2
  }

  def f2(x1: Boolean, x2: Int, x3: Int): Int = {
    if (x1) {
      x2
    } else {
      x3
    }
  }

  def f3(x1: Int, x2: Int, x3: Int): Int = {
    x1 match {
      case 1 =>
        x2
      case _ =>
        x3
    }
  }
}
