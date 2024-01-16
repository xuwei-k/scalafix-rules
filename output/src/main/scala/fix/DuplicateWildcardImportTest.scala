package fix

import scala.util._
import scala.collection.immutable.*

trait DuplicateWildcardImportTest {
  def x1: Success[Int]
  def x2: Failure[Int]
  def x3: Try[Int]
  def x4: Random
  def x5: BitSet
  def x6: IntMap[String]
}
