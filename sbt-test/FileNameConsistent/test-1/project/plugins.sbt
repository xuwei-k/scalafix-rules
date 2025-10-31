addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % sys.props("scalafix.version"))
addSbtPlugin("com.github.xuwei-k" % "warning-diff-scalafix-plugin" % "0.6.1")
libraryDependencies += "io.github.argonaut-io" %% "argonaut" % "6.3.11"
