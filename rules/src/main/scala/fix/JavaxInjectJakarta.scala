package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scala.meta.Importee
import scala.meta.Importer
import scala.meta.Name
import scala.meta.Term
import scala.meta.Type
import scala.meta.XtensionCollectionLikeUI
import scalafix.lint.Diagnostic
import scalafix.lint.LintSeverity
import scalafix.patch.Patch
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scalafix.v1.XtensionSeqPatch

case class JavaxInjectJakartaConfig(
  message: String,
  allowProvider: Boolean
)

object JavaxInjectJakartaConfig {
  val default: JavaxInjectJakartaConfig = JavaxInjectJakartaConfig(
    message = "use jakarta.inject",
    allowProvider = true,
  )

  implicit val surface: Surface[JavaxInjectJakartaConfig] =
    metaconfig.generic.deriveSurface[JavaxInjectJakartaConfig]

  implicit val decoder: ConfDecoder[JavaxInjectJakartaConfig] =
    metaconfig.generic.deriveDecoder(default)
}

object JavaxInjectJakarta {
  private val classNames: Set[String] = Set("Inject", "Named", "Qualifier", "Scope", "Singleton")
  private val classNamesWithProvider: Set[String] = classNames + "Provider"
}

class JavaxInjectJakarta(config: JavaxInjectJakartaConfig) extends SyntacticRule("JavaxInjectJakarta") {
  def this() = this(JavaxInjectJakartaConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("JavaxInjectJakarta")(this.config).map(newConfig => new JavaxInjectJakarta(newConfig))
  }

  private def provider: String = "Provider"

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Importer(
            Term.Select(Term.Name("javax"), Term.Name("inject")),
            values,
          )
          if values.collect { case Importee.Name(x) if x.value != provider => () }.nonEmpty || !config.allowProvider =>
        t
      case t @ Type.Select(
            Term.Select(Term.Name("javax"), Term.Name("inject")),
            Type.Name(x)
          ) if (x != provider) || !config.allowProvider =>
        t
      case t @ Type.Select(
            Term.Select(
              Term.Select(Term.Name("com"), Term.Name("google")),
              Term.Name("inject")
            ),
            Type.Name(x)
          ) if JavaxInjectJakarta.classNames(x) =>
        t
      case t @ Type.Select(
            Term.Select(
              Term.Select(Term.Name("com"), Term.Name("google")),
              Term.Name("inject")
            ),
            Type.Name("Provider")
          ) if !config.allowProvider =>
        t
      case t @ Term.Select(
            Term.Select(Term.Name("javax"), Term.Name("inject")),
            Term.Name(x)
          ) if (x != provider) || !config.allowProvider =>
        t
      case t @ Importer(
            Term.Select(
              Term.Select(Term.Name("com"), Term.Name("google")),
              Term.Name("inject")
            ),
            values
          ) if (if (config.allowProvider) {
                  values.collect {
                    case Importee.Name(x) if JavaxInjectJakarta.classNames(x.value) => ()
                  }.nonEmpty
                } else {
                  values.collect {
                    case Importee.Name(x) if JavaxInjectJakarta.classNamesWithProvider(x.value) => ()
                  }.nonEmpty
                }) =>
        t
      case t @ Importer(
            Term.Select(
              Term.Select(
                Term.Select(Term.Name("com"), Term.Name("google")),
                Term.Name("inject")
              ),
              Term.Name("name")
            ),
            List(Importee.Name(Name("Named")))
          ) =>
        t
      case t @ Type.Select(
            Term.Select(
              Term.Select(
                Term.Select(Term.Name("com"), Term.Name("google")),
                Term.Name("inject")
              ),
              Term.Name("name")
            ),
            Type.Name("Named")
          ) =>
        t
    }.map(t =>
      Patch.lint(
        Diagnostic(
          id = "",
          message = config.message,
          position = t.pos,
          severity = LintSeverity.Warning
        )
      )
    ).asPatch
  }
}
