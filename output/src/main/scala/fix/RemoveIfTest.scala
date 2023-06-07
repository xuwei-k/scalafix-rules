package fix

class RemoveIfTest {
  def f1[A](a1: A, a2: A): Boolean = {
    a1 == a2
  }

  def f2[A](a1: A, a2: A): Boolean = {
    a1 == a2
  }

  def f3[A](a1: A, a2: A): Boolean = {
    a1 != a2
  }

  def f4[A](a1: Seq[A]): Boolean = {
    a1.isEmpty
  }

  def f5[A](a1: Seq[A]): Boolean = {
    a1.isEmpty
  }
}
