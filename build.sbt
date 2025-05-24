import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val rulesCrossVersions = Seq(V.scala213, V.scala212)
lazy val scala3Version = "3.3.6"

val commonSettings = Def.settings(
  (Compile / packageSrc / mappings) ++= (Compile / managedSources).value.map { f =>
    // to merge generated sources into sources.jar as well
    (f, f.relativeTo((Compile / sourceManaged).value).get.getPath)
  },
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-language:higherKinds",
          "-Xsource:3",
        )
      case "2.13" =>
        Seq(
          "-Xsource:3-cross",
        )
      case _ =>
        Nil
    }
  },
  scalacOptions ++= Seq(
    "-Wconf:origin=scala.collection.compat.*:silent",
    "-deprecation",
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
  releaseStepCommandAndRemaining("sonaUpload"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

commonSettings

publish / skip := true

lazy val resourceGenSettings = Def.settings(
  Compile / resourceGenerators += Def.task {
    val rules = (Compile / compile).value
      .asInstanceOf[sbt.internal.inc.Analysis]
      .apis
      .internal
      .collect {
        case (className, analyzed) if analyzed.api.classApi.structure.parents.collect { case p: xsbti.api.Projection =>
              p.id
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

lazy val myRuleRule = project.settings(
  commonSettings,
  scalaVersion := V.scala212,
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
  resourceGenSettings,
  publish / skip := true
)

lazy val rules = projectMatrix
  .settings(
    commonSettings,
    moduleName := "scalafix-rules",
    publishTo := (if (isSnapshot.value) None else localStaging.value),
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
    libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.19" % Test,
    scalacOptions += {
      scalaBinaryVersion.value match {
        case "2.12" =>
          "-Ywarn-unused:imports"
        case _ =>
          "-Wunused:imports"
      }
    },
    Compile / sourceGenerators += task {
      val dir = (Compile / sourceManaged).value
      Seq[(String, Seq[String])](
        "DiscardSlickDBIO" -> Seq(
          "slick/dbio/package.DBIO#",
          "slick/dbio/DBIOAction#",
        ),
        "DiscardScalaFuture" -> Seq("scala/concurrent/Future#"),
        "DiscardMonixTask" -> Seq("monix/eval/Task#"),
        "DiscardEff" -> Seq("org/atnos/eff/Eff#"),
        "DiscardCatsEffectIO" -> Seq("cats/effect/IO#"),
      ).map { case (ruleName, types) =>
        val f = dir / "fix" / s"${ruleName}.scala"
        IO.write(
          f,
          s"""package fix
          |
          |import scalafix.Patch
          |import scalafix.v1.Configuration
          |import scalafix.v1.Rule
          |import scalafix.v1.SemanticDocument
          |import scalafix.v1.SemanticRule
          |import metaconfig.Configured
          |
          |class ${ruleName}(config: DiscardSingleConfig) extends SemanticRule("${ruleName}") {
          |
          |  def this() = this(DiscardSingleConfig.default)
          |
          |  override def withConfiguration(config: Configuration): Configured[Rule] =
          |    config.conf.getOrElse("${ruleName}")(this.config).map(newConfig => new ${ruleName}(newConfig))
          |
          |  override def fix(implicit doc: SemanticDocument): Patch =
          |    DiscardValue.typeRef(config.toDiscardValueConfig(${types.map("\"" + _ + "\"")}))
          |
          |}
          |""".stripMargin
        )
        f
      }
    },
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
    resourceGenSettings,
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(rulesCrossVersions)

lazy val rules212 = rules
  .jvm(V.scala212)
  .enablePlugins(ScriptedPlugin)
  .enablePlugins(ScalafixPlugin)
  .dependsOn(myRuleRule % ScalafixConfig)
  .settings(
    Test / test := (Test / test).dependsOn(scripted.toTask("")).value,
    Compile / compile := (Compile / compile).dependsOn((Compile / scalafix).toTask(" MyScalafixRuleRule")).value,
    scriptedBufferLog := false,
    scriptedLaunchOpts += ("-Dscalafix-rules.version=" + version.value),
    scriptedLaunchOpts += ("-Dscalafix.version=" + _root_.scalafix.sbt.BuildInfo.scalafixVersion),
    sbtTestDirectory := (LocalRootProject / baseDirectory).value / "sbt-test",
    scriptedLaunchOpts ++= {
      import scala.collection.JavaConverters.*
      val javaVmArgs: List[String] =
        java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
      javaVmArgs.filter(a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith))
    }
  )

lazy val inputOutputCommon = Def.settings(
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.13" =>
        Seq("-Wconf:cat=scala3-migration:info")
      case "3" =>
        Seq("-Ykind-projector")
      case _ =>
        Nil
    }
  },
  libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.13.0",
  libraryDependencies ++= {
    if (scalaBinaryVersion.value != "3") {
      Seq(compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full))
    } else {
      Nil
    }
  },
  libraryDependencies ++= {
    if (scalaBinaryVersion.value == "2.13") {
      Seq("io.circe" %% "circe-generic-extras" % "0.14.4")
    } else {
      Nil
    }
  },
  libraryDependencies += "com.typesafe.slick" %% "slick" % "3.6.1",
  libraryDependencies += "io.monix" %% "monix-eval" % "3.4.1",
  libraryDependencies += "org.mockito" % "mockito-subclass" % "5.18.0",
  libraryDependencies += "org.atnos" %% "eff-core" % "7.0.6"
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
    scalaVersion := V.scala212,
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

scalaVersion := V.scala212
