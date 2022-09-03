/*
rule = SyntacticOrganizeImports
 */
package fix

import scala.collection.concurrent
import scala.util.Random // assert: SyntacticOrganizeImports
import scala.collection.mutable
// assert: SyntacticOrganizeImports
import scala.collection.mutable.SortedMap
import scala.collection.mutable.SortedSet

class SyntacticOrganizeImports
