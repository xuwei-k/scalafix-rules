package foo

import scala.util.Try

/**
 * x2 object
 */
object X2

/**
 * x1
 */
trait X1 {
  def y: Try[Int]
}

// x2 class
class X2

object A
