/*
rule = JavaNioFileFilesWriteString
 */
package fix

import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

class JavaNioFileFilesWriteStringTest {
  def f1(p: Path, s: String): Path = Files.write(p, s.getBytes("UTF-8"))
  def f2(p: Path, s: String): Path = Files.write(p, s.getBytes(StandardCharsets.UTF_8))
  def f3(p: Path, s: String): Path = Files.write(p, s.getBytes(UTF_8))
  def f4(p: Path, s: String): Path = Files.write(p, s.getBytes(StandardCharsets.UTF_16))
  def f5(p: Path, s: String): Path = java.nio.file.Files.write(p, s.getBytes(UTF_8))
}
