ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % sys.props("scalafix-rules.version")

TaskKey[Unit]("check") := {
  if(!scala.util.Properties.isWin) {
    val actual = (LocalRootProject / warningsCurrentFile).value
    val expect = "expect.json"
    sys.process.Process(Seq("diff", actual.getAbsolutePath, expect)).!
    assert(IO.read(actual) == IO.read(file(expect)))
  }
}

scalaVersion := "2.12.21"
