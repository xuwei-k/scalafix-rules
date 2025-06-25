package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Importer
import scala.meta.Term
import scala.meta.XtensionCollectionLikeUI
import scalafix.Patch
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

final case class CatsInstancesImportConfig(
  message: String
)

object CatsInstancesImportConfig {
  val default: CatsInstancesImportConfig = CatsInstancesImportConfig(
    message = "unnecessary import https://github.com/typelevel/cats/pull/3043"
  )

  implicit val surface: Surface[CatsInstancesImportConfig] =
    metaconfig.generic.deriveSurface[CatsInstancesImportConfig]

  implicit val decoder: ConfDecoder[CatsInstancesImportConfig] =
    metaconfig.generic.deriveDecoder(default)
}
class CatsInstancesImport(config: CatsInstancesImportConfig) extends SyntacticRule("CatsInstancesImport") {

  def this() = this(CatsInstancesImportConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("CatsInstancesImport")(this.config).map(newConfig => new CatsInstancesImport(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Importer(
            Term.Select(
              Term.Select(Term.Name("cats"), Term.Name("instances")),
              _
            ),
            _
          ) =>
        Patch.lint(
          Diagnostic(
            id = "",
            message = config.message,
            position = t.pos,
            severity = LintSeverity.Warning
          )
        )
    }
  }.asPatch
}
