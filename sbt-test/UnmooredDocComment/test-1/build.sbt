ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % sys.props("scalafix-rules.version")

TaskKey[Unit]("check") := {
  if(!scala.util.Properties.isWin) {
    val actual = "target/warnings/warnings.json"
    val expect = "expect.json"
    sys.process.Process(Seq("diff", actual, expect)).!
    assert(IO.read(file(actual)) == IO.read(file(expect)))
  }
}
