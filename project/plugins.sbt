addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.5")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.6")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

addSbtPlugin("com.github.xuwei-k" % "sbt-root-aggregate" % "0.1.0")

addSbtPlugin("com.github.xuwei-k" % "scalafix-rule-resource-gen" % "0.1.1")

conflictWarning := {
  if (scalaBinaryVersion.value == "3") {
    ConflictWarning("warn", Level.Warn, false)
  } else {
    conflictWarning.value
  }
}
