package fix

abstract class UnnecessarySortRewriteTestAddImport2 {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.minBy(_._1)
    seq.maxBy(_._2)
  }
}
