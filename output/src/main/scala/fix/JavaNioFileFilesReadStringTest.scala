package fix

import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

class JavaNioFileFilesReadStringTest {
  def f1(p: Path): String = Files.readString(p)
  def f2(p: Path): String = Files.readString(p)
  def f3(p: Path): String = Files.readString(p)
  def f4(p: Path): String = Files.readString(p, StandardCharsets.UTF_16)
  def f5(p: Path): String = java.nio.file.Files.readString(p)
  def f6(p: Path): String = Files.readString(p, java.nio.charset.Charset.forName("UTF-16"))
  def f7(p: Path): String = Files.readString(p)
}
