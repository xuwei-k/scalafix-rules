/*
rule = ScalaUtilUsingResource
 */
package fix

import java.io.FileInputStream

object ScalaUtilUsingResourceTest {
  def f1: Int = {
    val a = new FileInputStream("a")
    try {
      val b = a.read()
      val c = a.read()
      b + c
    } finally {
      a.close()
    }
  }

  def f2: Int = {
    val a = new FileInputStream("a")
    try {
      val b = a.read()
      val c = a.read()
      b + c
    } finally
      a.close
  }

  def f3: String = {
    val a = new FileInputStream("a")
    try
      a.toString
    finally
      a.close()
  }

  def f4: Int = {
    val a1 = new FileInputStream("1")
    try {
      val a2 = new FileInputStream("2")
      try {
        a1.read + a2.read
      } finally
        a2.close()
    } finally
      a1.close()
  }

  def f5: String = {
    val a = new FileInputStream("a")
    val x =
      try
        a.toString
      finally
        a.close()

    s"${x}b"
  }
}
