package fix

/*
rule = UnnecessarySortRewrite
UnnecessarySortRewrite.rewriteConfig = addCompatImport
 */
abstract class UnnecessarySortRewriteTestAddImport2 {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.sortBy(_._1).head
    seq.sortBy(_._2).last
  }
}
