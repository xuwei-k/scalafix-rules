/*
rule = RemoveIf
 */
package fix

class RemoveIfTest {
  def f1[A](a1: A, a2: A): Boolean = {
    if (a1 == a2) {
      true
    } else {
      false
    }
  }

  def f2[A](a1: A, a2: A): Boolean = {
    if (a1 != a2) {
      false
    } else {
      true
    }
  }

  def f3[A](a1: A, a2: A): Boolean = {
    if (a1 == a2)
      false
    else
      true
  }

  def f4[A](a1: Seq[A]): Boolean = {
    if (a1.isEmpty)
      true
    else
      false
  }

  def f5[A](a1: Seq[A]): Boolean = {
    if (a1.nonEmpty)
      false
    else
      true
  }
}
