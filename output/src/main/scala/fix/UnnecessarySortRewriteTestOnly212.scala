package fix


abstract class UnnecessarySortRewriteTestOnly212 {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.minBy(_._1)
    seq.maxBy(_._2)
    seq.sortBy(_._1).headOption
    seq.sortBy(_._2).lastOption
  }
}
