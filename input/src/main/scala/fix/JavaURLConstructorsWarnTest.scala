/*
rule = JavaURLConstructorsWarn
 */
package fix

import java.net.URL
import java.net.{URL => JavaNetURL}

class JavaURLConstructorsWarnTest {
  def x1 = new URL("https://example.com") // assert: JavaURLConstructorsWarn
  def x2 = new JavaNetURL("https://example.com") // assert: JavaURLConstructorsWarn
  def x3 = new java.net.URL("https://example.com") // assert: JavaURLConstructorsWarn

  object X {
    class URL(x: String)

    def x4 = new URL("https://example.com")
  }
}
