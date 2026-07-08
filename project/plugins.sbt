addSbtPlugin("com.eed3si9n" % "sbt-salad-days" % "0.2.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.7")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.1")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.5.0")

addSbtPlugin("com.github.xuwei-k" % "scalafix-rule-resource-gen" % "0.1.3")

scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-feature",
)
