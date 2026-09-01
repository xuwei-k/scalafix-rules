/*
rule = MapPartitionMap
 */
package fix

object MapPartitionMapTest {
  def f1[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.map(f).partitionMap(x => x) // assert: MapPartitionMap

  def f2[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.map(f).partitionMap(identity) // assert: MapPartitionMap

  def f3[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.map(f).partitionMap { x => x } // assert: MapPartitionMap
}
