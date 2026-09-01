/*
rule = MapPartitionMapIdentity
 */
package fix

object MapPartitionMapIdentityTest {
  def f1[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.map(f).partitionMap(x => x)

  def f2[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.map(f).partitionMap(identity)

  def f3[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.map(f).partitionMap { x => x }
}
