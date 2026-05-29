package fix

import org.scalatest.funsuite.AnyFunSuite
import scala.meta.inputs.Input
import scalafix.internal.config.ScalaVersion
import scalafix.patch.Patch
import scalafix.v1.SyntacticDocument

class UnnecessaryMatchTest extends AnyFunSuite {
  test("PolyFunction") {
    val x = new UnnecessaryMatch
    val in = SyntacticDocument.fromInput(
      input = Input.String("""
        |def x = f([b] =>
        |  a =>
        |    a match {
        |      case 1 => 2
        |      case 3 => 4
        |    }
        |)
        |""".stripMargin),
      scalaVersion = ScalaVersion.scala3
    )
    val result = x.fix(in)
    assert(result == Patch.empty)
  }
}
