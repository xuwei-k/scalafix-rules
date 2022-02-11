package fix

import metaconfig.generic.Surface
import metaconfig.ConfDecoder
import metaconfig.Configured
import scalafix.Diagnostic
import scalafix.Patch
import scalafix.lint.LintSeverity
import scalafix.v1.Configuration
import scalafix.v1.Rule
import scalafix.v1.SyntacticDocument
import scalafix.v1.SyntacticRule
import scala.meta.Pkg
import scala.meta.Term
import scala.meta.Tree
import scala.meta.inputs.Input
import scala.meta.inputs.Position

case class DirectoryAndPackageNameConfig(
  baseDirectory: Seq[String],
  keyword: Seq[String]
)

object DirectoryAndPackageNameConfig {
  val default: DirectoryAndPackageNameConfig = DirectoryAndPackageNameConfig(
    baseDirectory = Seq(
      "/src/main/scala-2.11/",
      "/src/test/scala-2.11/",
      "/src/main/scala-2.12/",
      "/src/test/scala-2.12/",
      "/src/main/scala-2.13/",
      "/src/test/scala-2.13/",
      "/src/main/scala-2/",
      "/src/test/scala-2/",
      "/src/main/scala-3/",
      "/src/test/scala-3/",
      "/src/main/scala/",
      "/src/test/scala/",
    ),
    keyword = Seq(
      "abstract",
      "case",
      "catch",
      "class",
      "def",
      "do",
      "else",
      "enum",
      "export",
      "extends",
      "false",
      "final",
      "finally",
      "for",
      "forSome",
      "if",
      "implicit",
      "import",
      "lazy",
      "macro",
      "match",
      "ne",
      "new",
      "null",
      "object",
      "override",
      "package",
      "private",
      "protected",
      "return",
      "sealed",
      "super",
      "then",
      "this",
      "throw",
      "trait",
      "try",
      "true",
      "type",
      "val",
      "var",
      "while",
      "with",
      "yield",
    )
  )

  implicit val surface: Surface[DirectoryAndPackageNameConfig] =
    metaconfig.generic.deriveSurface[DirectoryAndPackageNameConfig]

  implicit val decoder: ConfDecoder[DirectoryAndPackageNameConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class DirectoryAndPackageName(config: DirectoryAndPackageNameConfig) extends SyntacticRule("DirectoryAndPackageName") {

  def this() = this(DirectoryAndPackageNameConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf
      .getOrElse("DirectoryAndPackageName")(this.config)
      .map(newConfig => new DirectoryAndPackageName(newConfig))
  }

  private[this] val keywords: Set[String] = config.keyword.map("`" + _ + "`").toSet

  override def isLinter = true

  private def getPackages(t: Tree): List[Term.Ref] = {
    t.collect { case p: Pkg => p.ref :: p.stats.flatMap(getPackages) }.flatten.distinct
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    val packages = getPackages(doc.tree)

    val packageObjectOpt =
      doc.tree.collect { case x: Pkg.Object =>
        x
      }.headOption

    val scalaSourceOpt = PartialFunction.condOpt(doc.input) {
      case f: Input.VirtualFile =>
        f.path
      case f: Input.File =>
        f.path.toString
    }

    {
      for {
        path <- scalaSourceOpt
        // TODO Windows
        if !scala.util.Properties.isWin
        dirOpt = config.baseDirectory.find { dir =>
          path.contains(dir)
        }.map { dir =>
          path.split(dir).last.split('/').init.mkString("/")
        }
        dir <- dirOpt
        if packages.nonEmpty // TODO check if empty package
        packageName = {
          val x = packages
            .flatMap(_.toString.split('.'))
            .map(p =>
              if (keywords(p)) { p.replace("`", "") }
              else p
            )
            .mkString("/")
          packageObjectOpt match {
            case Some(value) =>
              x + "/" + value.name.value
            case None =>
              x
          }
        }
        if packageName != dir
      } yield {
        Patch.lint(
          DirectoryPackageWarn(
            path = path,
            packageName = packageName,
            position = packages.last.pos
          )
        )
      }
    }.getOrElse(Patch.empty)
  }
}

case class DirectoryPackageWarn(path: String, packageName: String, override val position: Position) extends Diagnostic {
  override def message = s"inconsistent package and directory\n${path}\n${packageName}"

  override def severity = LintSeverity.Warning
}
