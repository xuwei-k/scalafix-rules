package fix

object MapPartitionMapIdentityTest {
  def f1[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.partitionMap(f)

  def f2[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.partitionMap(f)

  def f3[A, B, C](list: List[A], f: A => Either[B, C]): (List[B], List[C]) =
    list.partitionMap(f) 
}
