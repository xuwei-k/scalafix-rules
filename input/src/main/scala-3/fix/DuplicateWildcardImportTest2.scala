/*
rule = DuplicateWildcardImport
 */
package fix

import scala.Ordering.*
import scala.Ordering.given Ordering[String]
import scala.util.*
import scala.util.given

trait DuplicateWildcardImportTest2
