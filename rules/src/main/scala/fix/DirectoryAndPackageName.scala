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
import java.io.File
import java.util.Locale
import scala.meta.Pkg
import scala.meta.Term
import scala.meta.Tree
import scala.meta.inputs.Input

case class DirectoryAndPackageNameConfig(
  baseDirectory: Seq[String],
  keyword: Seq[String],
  severity: LintSeverity
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
    ),
    severity = LintSeverity.Warning
  )

  implicit val surface: Surface[DirectoryAndPackageNameConfig] =
    metaconfig.generic.deriveSurface[DirectoryAndPackageNameConfig]

  implicit val decoder: ConfDecoder[DirectoryAndPackageNameConfig] = {
    implicit val instance: metaconfig.ConfDecoder[scalafix.lint.LintSeverity] = { conf =>
      conf.as[String].map(_.toUpperCase(Locale.ROOT)).map {
        case "ERROR" =>
          LintSeverity.Error
        case "INFO" =>
          LintSeverity.Info
        case _ =>
          LintSeverity.Warning
      }
    }

    metaconfig.generic.deriveDecoder(default)
  }
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

    val scalaSourceOpt = PartialFunction
      .condOpt(doc.input) {
        case f: Input.VirtualFile =>
          f.path
        case f: Input.File =>
          f.path.toString
      }
      .map { path =>
        File.separatorChar match {
          case '/' =>
            path
          case c =>
            path.replace(c, '/')
        }
      }

    {
      for {
        path <- scalaSourceOpt
        if packages.nonEmpty // TODO check if empty package
        packageName = {
          val x = packages
            .flatMap(_.toString.split('.'))
            .map(p =>
              if (keywords(p)) { p.replace("`", "") }
              else p
            )
          packageObjectOpt match {
            case Some(value) =>
              x :+ value.name.value
            case None =>
              x
          }
        }
        if config.baseDirectory.find { dir =>
          path.contains(dir)
        }.exists { fullDir =>
          val splitedDir = path.split(fullDir).last.split('/').init.toList
          packageName != splitedDir
        } || {
          path.split('/').init.toList.takeRight(packageName.size) != packageName
        }
      } yield {
        Patch.lint(
          Diagnostic(
            id = "",
            message = s"inconsistent package and directory\n${path}\n${packageName.mkString("/")}",
            position = packages.last.pos,
            severity = config.severity
          )
        )
      }
    }.getOrElse(Patch.empty)
  }
}
