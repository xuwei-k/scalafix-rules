/*
rule = CatsInstancesImport
 */
package fix

import cats.Monad
import cats.instances.all._ // assert: CatsInstancesImport

trait CatsInstancesImportTest {
  def f: Monad[List] = Monad[List]
}
