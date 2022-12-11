package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Import
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Token
import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule

case class DuplicateWildcardImportConfig(isScala3: Boolean)

object DuplicateWildcardImportConfig {
  val default = DuplicateWildcardImportConfig(isScala3 = false)
  implicit val surface: Surface[DuplicateWildcardImportConfig] =
    metaconfig.generic.deriveSurface[DuplicateWildcardImportConfig]
  implicit val decoder: ConfDecoder[DuplicateWildcardImportConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class DuplicateWildcardImport(conf: DuplicateWildcardImportConfig) extends SyntacticRule("DuplicateWildcardImport") {
  def this() = this(DuplicateWildcardImportConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    val isScala3 = config.scalaVersion.startsWith("3") || config.scalacOptions.contains("-Xsource:3")
    config.conf
      .getOrElse("DuplicateWildcardImport")(DuplicateWildcardImportConfig(isScala3))
      .map(new DuplicateWildcardImport(_))
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect { case t: Source =>
      // ignore `import a.b.{x => y}`
      val exclude = {
        Seq("{", "=>", "}") ++ {
          if (conf.isScala3) {
            Seq(" as ")
          } else {
            Nil
          }
        }
      }
      def isWildcard(s: String): Boolean = {
        s.endsWith("._") || (conf.isScala3 && s.endsWith(".*"))
      }

      t.stats.collect { case p: Pkg =>
        p.stats
      }.flatten.collect { case i: Import =>
        i
      }.groupBy(i => i.toString.split('.').init.mkString("."))
        .values
        .filter(x => x.size > 1 && x.exists(a => isWildcard(a.toString)))
        .flatMap {
          _.filterNot { x =>
            val s = x.toString
            isWildcard(s) || exclude.exists(s contains _)
          }.map { x =>
            Seq(
              Patch.removeTokens(x.tokens),
              Patch.removeTokens(
                doc.tree.tokens.dropWhile(_.pos.start < x.tokens.last.pos.end).headOption.filter(_.is[Token.LF])
              )
            ).asPatch
          }
        }
        .asPatch
    }.asPatch
  }
}
