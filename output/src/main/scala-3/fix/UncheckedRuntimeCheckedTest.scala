package fix

class UncheckedRuntimeCheckedTest {
  extension [A](a: A) {
    def runtimeChecked: A = a
  }

  def f1[A](list: List[A]): A = (list.runtimeChecked ) match {
    case x1 :: x2 => x1
  }

  def f2[A](list: List[A]): Int = list match {
    case x: List[Int @unchecked] =>
      x.head
  }
}
