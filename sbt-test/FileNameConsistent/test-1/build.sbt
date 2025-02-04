import argonaut._

ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % sys.props("scalafix-rules.version")

TaskKey[Unit]("check") := {
  val actual = JsonParser.parse(IO.read(file("target/warnings/warnings.json"))).fold(sys.error(_), identity)
  val expect = Json.array(
    Json.obj(
      "message" -> Json.jString("[FileNameConsistent] inconsistent file name and class name. names = [Y]"),
      "position" -> Json.obj(
        "line" -> Json.jNumber(3),
        "lineContent" -> Json.jString("class Y"),
        "sourcePath" -> Json.jString(
          if (scala.util.Properties.isWin) {
            "${BASE}/src\\main\\scala\\a\\X.scala"
          } else {
            "${BASE}/src/main/scala/a/X.scala"
          }
        ),
        "startLine" -> Json.jNumber(3),
        "startColumn" -> Json.jNumber(0),
        "endLine" -> Json.jNumber(3),
        "endColumn" -> Json.jNumber(7)
      )
    )
  )
  assert(actual == expect, s"$actual != $expect")
}
