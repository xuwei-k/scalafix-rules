package fix

import scala.meta.Import
import scala.meta.Pkg
import scala.meta.Source
import scalafix.Patch
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

class DuplicateWildcardImport extends SyntacticRule("DuplicateWildcardImport") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Source =>
      val exclude = Seq("{", "=>", "}") // ignore `import a.b.{x => y}`
      t.stats.collect { case p: Pkg =>
        p.stats
      }.flatten.collect { case i: Import =>
        i
      }.groupBy(i => i.toString.split('.').init.mkString("."))
        .values
        .filter(x => x.size > 1 && x.exists(_.toString.endsWith("._")))
        .flatMap {
          _.filterNot { x =>
            val s = x.toString
            s.endsWith("._") || exclude.exists(s contains _)
          }.map { x =>
            Patch.removeTokens(x.tokens)
          }
        }
        .asPatch
    }.asPatch
  }
}
