package fix

import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

class JavaNioFileFilesWriteStringTest {
  def f1(p: Path, s: String): Path = Files.writeString(p, s)
  def f2(p: Path, s: String): Path = Files.writeString(p, s)
  def f3(p: Path, s: String): Path = Files.writeString(p, s)
  def f4(p: Path, s: String): Path = Files.write(p, s.getBytes(StandardCharsets.UTF_16))
  def f5(p: Path, s: String): Path = java.nio.file.Files.writeString(p, s)
}
