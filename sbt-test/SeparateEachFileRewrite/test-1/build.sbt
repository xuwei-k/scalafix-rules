ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % sys.props("scalafix-rules.version")

TaskKey[Unit]("check") := {
  val names = Set("X1.scala", "X2.scala", "A.scala")
  val actualNames = file("src/main/scala/foo").listFiles().map(_.getName).toSet
  assert(actualNames == names, actualNames)
  if(!scala.util.Properties.isWin) {
    names.foreach {
      f =>
        val actual = IO.read(file(s"src/main/scala/foo/${f}"))
        val expect = IO.read(file(s"expect/${f}"))
        sys.process.Process(Seq("diff", s"src/main/scala/foo/${f}", s"expect/${f}")).!
        assert(actual == expect, actual)
    }
  }
}

scalaVersion := "2.12.21"
