package fix

import org.scalatest.funsuite.AnyFunSuite
import scalafix.internal.config.ScalaVersion
import scalafix.lint.LintSeverity
import scalafix.v1.SyntacticDocument
import java.io.File
import scala.meta.inputs.Input

class IncorrectScaladocTypeParamTest extends AnyFunSuite {
  test("test") {
    val x = new IncorrectScaladocTypeParam
    val in = SyntacticDocument.fromInput(
      input = Input.File(new File("rules/src/test/scala-3/fix/TypeParamTest1.scala")),
      scalaVersion = ScalaVersion.scala3
    )
    val result = x.fix0(in)
    result.foreach { x =>
      assert(x.message == "incorrect @tparam")
      assert(x.severity == LintSeverity.Warning)
    }
    val values =
      result
        .map(x =>
          Input
            .Slice(
              input = in.input,
              start = x.position.start,
              end = x.position.end
            )
            .text
        )
        .toSet
    assert(
      values == Set(
        "  * @tparam A0 a0",
        "  * @tparam A2 a2",
        "    * @tparam B2 b2",
        "    * @tparam C1 c1",
        "  * @tparam F3 f3",
        "  * @tparam E3 e3",
        "    * @tparam E4 e4",
      )
    )
  }
}
