package fix

import java.io.File
import java.util.regex.Pattern
import org.scalatest.funsuite.AnyFunSuite
import sbt.io.IO
import scala.io.Source

class RuleIndependentTest extends AnyFunSuite {
  private def fromResource(fileName: String): List[String] =
    Source.fromInputStream(this.getClass.getResourceAsStream(s"/${fileName}"), "UTF-8").getLines().toList

  private val exclude: Set[String] = Set(
    "DiscardSingleConfig",
    "SeparateEachFileRewrite",
    "SeparateEachFileWarn",
    "UnnecessarySortRewrite",
    "DiscardCatsEffectIO",
    "DiscardEff",
    "DiscardMonixTask",
    "DiscardScalaFuture",
    "DiscardSlickDBIO",
  ).map(_ + ".scala")

  private val fileNames: List[String] =
    fromResource("main-sources.txt")

  private val scalacOptions: List[String] =
    fromResource("scalac-options.txt")

  private val mainExternalClasspath: String =
    fromResource("main-external-classpath.txt").mkString(File.pathSeparator)

  fileNames.foreach { f =>
    val file = f.split(Pattern.quote(File.separator)).last
    test(file) {
      if (exclude(file)) {
        pending
      } else {
        val result = IO.withTemporaryDirectory { dir =>
          scala.tools.nsc.Main.process(
            Seq(
              scalacOptions,
              Seq(
                "-cp",
                mainExternalClasspath,
                f,
                "-d",
                dir.getCanonicalPath
              )
            ).flatten.toArray
          )
        }
        assert(result)
      }
    }
  }
}
