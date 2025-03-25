/*
rule = CatsImplicitsImport
 */
package fix

import cats.implicits._ // assert: CatsImplicitsImport

trait CatsImplicitsImportTest {
  def f[A](x: List[A]): List[Unit] = x.void
}
