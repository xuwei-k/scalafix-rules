import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val rulesCrossVersions = Seq(V.scala213, V.scala212)
lazy val scala3Version = "3.2.0"

val commonSettings = Def.settings(
  scalacOptions ++= {
    if (scalaBinaryVersion.value == "3") {
      Nil
    } else {
      Seq(
        "-Xsource:3",
      )
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-language:higherKinds",
    "-feature",
  ),
  pomExtra := (
    <developers>
    <developer>
      <id>xuwei-k</id>
      <name>Kenji Yoshida</name>
      <url>https://github.com/xuwei-k</url>
    </developer>
  </developers>
  <scm>
    <url>git@github.com:xuwei-k/scalafix-rules.git</url>
    <connection>scm:git:git@github.com:xuwei-k/scalafix-rules.git</connection>
  </scm>
  ),
  description := "scalafix rules",
  organization := "com.github.xuwei-k",
  homepage := Some(url("https://github.com/xuwei-k/scalafix-rules")),
  licenses := List(
    "MIT License" -> url("https://opensource.org/licenses/mit-license")
  ),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommandAndRemaining("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

commonSettings

publish / skip := true

lazy val rules = projectMatrix
  .settings(
    commonSettings,
    moduleName := "scalafix-rules",
    publishTo := sonatypePublishToBundle.value,
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
    Compile / doc / scalacOptions ++= {
      val hash = sys.process.Process("git rev-parse HEAD").lineStream_!.head
      if (scalaBinaryVersion.value != "3") {
        Seq(
          "-sourcepath",
          (LocalRootProject / baseDirectory).value.getAbsolutePath,
          "-doc-source-url",
          s"https://github.com/xuwei-k/scalafix-rules/blob/${hash}€{FILE_PATH}.scala"
        )
      } else {
        Nil
      }
    },
    Compile / resourceGenerators += Def.task {
      val rules = (Compile / compile).value
        .asInstanceOf[sbt.internal.inc.Analysis]
        .apis
        .internal
        .collect {
          case (className, analyzed) if analyzed.api.classApi.structure.parents.collect {
                case p: xsbti.api.Projection => p.id
              }.exists(Set("SyntacticRule", "SemanticRule")) =>
            className
        }
        .toList
        .sorted
      assert(rules.nonEmpty)
      val output = (Compile / resourceManaged).value / "META-INF" / "services" / "scalafix.v1.Rule"
      IO.writeLines(output, rules)
      Seq(output)
    }.taskValue,
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(rulesCrossVersions)

lazy val inputOutputCommon = Def.settings(
  libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1",
  libraryDependencies ++= {
    if (scalaBinaryVersion.value != "3") {
      Seq(compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full))
    } else {
      Nil
    }
  },
  libraryDependencies ++= {
    if (scalaBinaryVersion.value == "2.13") {
      Seq("io.circe" %% "circe-generic-extras" % "0.14.3")
    } else {
      Nil
    }
  },
  libraryDependencies += "com.typesafe.slick" %% "slick" % "3.4.1" cross CrossVersion.for3Use2_13 exclude ("org.scala-lang.modules", "scala-collection-compat_2.13"),
  libraryDependencies += "org.atnos" %% "eff" % "6.0.1"
)

lazy val input = projectMatrix
  .settings(
    commonSettings,
    inputOutputCommon,
    scalacOptions ++= {
      if (scalaBinaryVersion.value == "2.13") {
        Seq(
          "-Ymacro-annotations"
        )
      } else {
        Nil
      }
    },
    publish / skip := true
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions :+ scala3Version)

lazy val output = projectMatrix
  .settings(
    commonSettings,
    inputOutputCommon,
    publish / skip := true
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions :+ scala3Version)

lazy val testsAggregate = Project("tests", file("target/testsAggregate"))
  .aggregate(tests.projectRefs: _*)
  .settings(
    commonSettings,
    publish / skip := true,
  )

lazy val tests = projectMatrix
  .settings(
    commonSettings,
    publish / skip := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitOutputSourceDirectories :=
      TargetAxis.resolve(output, Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputSourceDirectories :=
      TargetAxis.resolve(input, Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputClasspath :=
      TargetAxis.resolve(input, Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions :=
      TargetAxis.resolve(input, Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion :=
      TargetAxis.resolve(input, Compile / scalaVersion).value
  )
  .defaultAxes(
    rulesCrossVersions.map(VirtualAxis.scalaABIVersion) :+ VirtualAxis.jvm: _*
  )
  .customRow(
    scalaVersions = Seq(V.scala212),
    axisValues = Seq(TargetAxis(scala3Version), VirtualAxis.jvm),
    settings = Seq()
  )
  .customRow(
    scalaVersions = Seq(V.scala213),
    axisValues = Seq(TargetAxis(V.scala213), VirtualAxis.jvm),
    settings = Seq()
  )
  .customRow(
    scalaVersions = Seq(V.scala212),
    axisValues = Seq(TargetAxis(V.scala212), VirtualAxis.jvm),
    settings = Seq()
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
