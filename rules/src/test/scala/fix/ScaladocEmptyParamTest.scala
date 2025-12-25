package fix

import org.scalatest.exceptions.TestPendingException
import org.scalatest.funsuite.AnyFunSuite
import scala.meta.inputs.Input
import scalafix.internal.config.ScalaVersion
import scalafix.v1.SyntacticDocument

class ScaladocEmptyParamTest extends AnyFunSuite {
  test("ScaladocEmptyParam") {
    if (scala.util.Properties.isWin) {
      throw new TestPendingException
    }
    val x = new ScaladocEmptyParam
    val str = """
          |/**
          |  * @param x1
          |  * @param yyy zzz
          |  */
          |abstract class A(x1: Int) {
          |
          | /**
          |   * @param z zzz
          |   * @param y
          |   */
          |  def this(y: S, z: S) = { this(3) }
          |
          | /**
          |   * @param y1 bbb
          |   * @param ccc
          |   */
          |  def f1(y1: Int): Int
          |
          | /**
          |  * @param y2 bbb
          |  * @param eee fff
          |  */
          |  def f2(y2: Int): Int = y2
          |
          | /**
          |   * @param y3
          |   * @param ggg hhh
          |   */
          |  def f3(y3: Int) = macro macroImpl
          |}
          |
          |/**
          |  * @param b
          |  *   c
          |  * @param a1
          |  * @param a2
          |  * @param a3
          |  *   aaa
          |  * @param a4
          |  * @param a5
          |  *   aaa
          |  * @param a6
          |  */
          |case class Issue440(a1: Int, a2: Int)
          |""".stripMargin
    val in = SyntacticDocument.fromInput(
      input = Input.String(str),
      scalaVersion = ScalaVersion.scala2
    )
    val result = x.fix0(in).flatMap { case (c, values) =>
      values.map(_ + c.pos.startLine)
    }
    assert(result == List(2, 9, 15, 26, 35, 36, 39, 42))
    val lines = str.linesIterator.zipWithIndex.collect {
      case (line, index) if result.contains(index) =>
        line
    }.toList
    assert(
      lines == Seq(
        "  * @param x1",
        "   * @param y",
        "   * @param ccc",
        "   * @param y3",
        "  * @param a1",
        "  * @param a2",
        "  * @param a4",
        "  * @param a6",
      )
    )
  }
}
