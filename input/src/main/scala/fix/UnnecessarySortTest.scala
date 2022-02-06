package fix

/*
rule = UnnecessarySort
 */
abstract class UnnecessarySortTest {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.sortBy(_._1).head /* assert: UnnecessarySort
    ^^^^^^^^^^^^^^^^^^^^^
maybe you can use minBy
     */

    seq.sortBy(_._2).last /* assert: UnnecessarySort
    ^^^^^^^^^^^^^^^^^^^^^
maybe you can use maxBy
     */

    seq.sortBy(_._1).headOption /* assert: UnnecessarySort
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^
maybe you can use minByOption
     */

    seq.sortBy(_._2).lastOption /* assert: UnnecessarySort
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^
maybe you can use maxByOption
     */
  }
}
