/*
rule = SyntacticOrganizeImports
 */
package fix

import scala.collection.mutable.{ArrayBuffer => A} // assert: SyntacticOrganizeImports
import scala.collection.mutable.SortedMap

class SyntacticOrganizeImportsTest4
