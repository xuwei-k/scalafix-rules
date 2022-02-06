package fix


abstract class UnnecessarySortTest {
  def seq: Seq[(Int, Int)]

  def foo: Unit = {
    seq.sortBy(_._1).head 

    seq.sortBy(_._2).last 

    seq.sortBy(_._1).headOption 

    seq.sortBy(_._2).lastOption 
  }
}
