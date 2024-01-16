/*
rule = DuplicateWildcardImport
 */
package fix

import scala.util._
import scala.util.Success
import scala.util.Failure
import scala.util.Try
import scala.util.Random
import scala.collection.immutable.*
import scala.collection.immutable.BitSet
import scala.collection.immutable.IntMap

trait DuplicateWildcardImportTest {
  def x1: Success[Int]
  def x2: Failure[Int]
  def x3: Try[Int]
  def x4: Random
  def x5: BitSet
  def x6: IntMap[String]
}
