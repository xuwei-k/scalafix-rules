package fix

abstract class UnnecessarySortRewriteTest {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.minBy(_._1)
    seq.maxBy(_._2)
    seq.minByOption(_._1)
    seq.maxByOption(_._2)
  }
}
