ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % sys.props("scalafix-rules.version")

Compile / unmanagedSourceDirectories += baseDirectory.value / "my-src-dir"

scalaVersion := "2.12.20"
