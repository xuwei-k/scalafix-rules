package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Import
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

final case class CatsImplicitsImportConfig(
  message: String
)

object CatsImplicitsImportConfig {
  val default: CatsImplicitsImportConfig = CatsImplicitsImportConfig(
    message = "use `cats.syntax` instead of `cats.implicits` https://github.com/typelevel/cats/issues/4138"
  )

  implicit val surface: Surface[CatsImplicitsImportConfig] =
    metaconfig.generic.deriveSurface[CatsImplicitsImportConfig]

  implicit val decoder: ConfDecoder[CatsImplicitsImportConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CatsImplicitsImport(config: CatsImplicitsImportConfig) extends SyntacticRule("CatsImplicitsImport") {

  def this() = this(CatsImplicitsImportConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("CatsImplicitsImport")(this.config).map(newConfig => new CatsImplicitsImport(newConfig))
  }
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Import(
            Importer(Term.Select(Term.Name("cats"), Term.Name("implicits")), _) :: Nil
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
