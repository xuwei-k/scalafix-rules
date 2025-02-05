ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % sys.props("scalafix-rules.version")

TaskKey[Unit]("check") := {
  def f(x: String) = argonaut.JsonParser.parse(IO.read(file(x))).fold(sys.error(_), identity)
  val actual = f("target/warnings/warnings.json")
  val expect = f("expect.json")
  println(actual)
  println(expect)
  assert(actual == expect, s"${actual} != ${expect}")
}
