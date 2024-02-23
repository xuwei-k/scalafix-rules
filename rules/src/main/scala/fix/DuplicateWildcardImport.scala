package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Import
import scala.meta.Importee
import scala.meta.Importer
import scala.meta.Pkg
import scala.meta.Source
import scala.meta.Token
import scala.meta.XtensionClassifiable
import scala.meta.XtensionCollectionLikeUI
import scala.meta.XtensionStructure
import scalafix.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

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
      def isRename(s: Importer) = {
        s.importees.exists(_.is[Importee.Rename])
      }
      def isWildcard(s: Importer): Boolean = {
        s.importees.exists(_.is[Importee.Wildcard])
      }

      t.stats.collect { case p: Pkg =>
        p.stats
      }.flatten.collect { case i: Import =>
        i -> i.importers
      }.collect { case (i, x :: Nil) =>
        i -> x
      }.groupBy { case (_, x) =>
        x.ref.structure
      }.values.filter { x =>
        x.size > 1 && x.exists(a => isWildcard(a._2))
      }.flatMap {
        _.filterNot { x =>
          isWildcard(x._2) || isRename(x._2)
        }.map { x =>
          Seq(
            Patch.removeTokens(x._1.tokens),
            Patch.removeTokens(
              doc.tree.tokens.dropWhile(_.pos.start < x._1.tokens.last.pos.end).headOption.filter(_.is[Token.LF])
            )
          ).asPatch
        }
      }.asPatch
    }.asPatch
  }
}
