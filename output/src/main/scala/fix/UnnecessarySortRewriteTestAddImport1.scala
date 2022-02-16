package fix



import scala.collection.compat._
abstract class UnnecessarySortRewriteTestAddImport1 {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.minBy(_._1)
    seq.maxBy(_._2)
    seq.minByOption(_._1)
    seq.maxByOption(_._2)
  }
}
