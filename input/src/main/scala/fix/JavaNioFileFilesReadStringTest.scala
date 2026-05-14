/*
rule = JavaNioFileFilesReadString
 */
package fix

import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

class JavaNioFileFilesReadStringTest {
  def f1(p: Path): String = new String(Files.readAllBytes(p), "UTF-8")
  def f2(p: Path): String = new String(Files.readAllBytes(p), StandardCharsets.UTF_8)
  def f3(p: Path): String = new String(Files.readAllBytes(p), UTF_8)
  def f4(p: Path): String = new String(Files.readAllBytes(p), StandardCharsets.UTF_16)
  def f5(p: Path): String = new String(java.nio.file.Files.readAllBytes(p), UTF_8)
  def f6(p: Path): String = new String(Files.readAllBytes(p), "UTF-16")
  def f7(p: Path): String = new java.lang.String(Files.readAllBytes(p), "UTF-8")
}
