import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val rulesCrossVersions = Seq(V.scala213, V.scala212)
lazy val scala3Version = "3.3.7"

val commonSettings = Def.settings(
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
  releaseStepCommandAndRemaining("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

commonSettings

publish / skip := true

lazy val myRuleRule = project
  .settings(
    commonSettings,
    scalaVersion := V.scala212,
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
    publish / skip := true
  )
  .enablePlugins(ScalafixRuleResourceGen)

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
  )
  .defaultAxes(VirtualAxis.jvm)
  .enablePlugins(ScalafixRuleResourceGen)
  .jvmPlatform(rulesCrossVersions)

val dogfooding = taskKey[Unit]("")

val scalafixRulesDependency = "com.github.xuwei-k" %% "scalafix-rules" % "0.6.19" % Test

// for scala-steward
lazy val dummy = project.settings(
  commonSettings,
  libraryDependencies += scalafixRulesDependency,
  publish / skip := true
)

lazy val rules212 = rules
  .jvm(V.scala212)
  .enablePlugins(ScriptedPlugin)
  .enablePlugins(ScalafixPlugin)
  .dependsOn(myRuleRule % ScalafixConfig)
  .settings(
    semanticdbEnabled := false,
    Test / test := (Test / test).dependsOn(scripted.toTask("")).value,
    dogfooding := Def.taskDyn {
      val rules: Seq[String] = Seq(
        "CaseClassImplicitVal",
        "CompareSameValue",
        "DirectoryAndPackageName",
        "DuplicateWildcardImport",
        "ExplicitImplicitTypes",
        "FileNameConsistent",
        "FilterSize",
        "FinalObjectWarn",
        "FutureUnit",
        "ImplicitClassNoParent",
        "ImplicitClassOnlyDef",
        "ImplicitImplicit",
        "ImplicitValueClass",
        "IncorrectScaladocParam",
        "InterpolationToStringWarn",
        "IntersectionType",
        "IsEmptyNonEmpty",
        "LeakingImplicitClassValAll",
        "MapDistinctSize",
        "MapFlattenFlatMap",
        "MapToForeach",
        "ObjectSelfType",
        "PartialFunctionCondOpt",
        "SameParamOverloading",
        "Scala3Keyword",
        "SimplifyForYield",
        "StringFormatToInterpolation",
        "UnmooredDocComment",
        "UnnecessaryMatch",
        "UnusedConstructorParams",
        "UnusedSelfType",
        "UnusedTypeParams",
        "UselessParamCommentsWarn",
      )

      assert(rules.distinct.sorted == rules)

      val arg = rules
        .map(x => s"dependency:${x}@com.github.xuwei-k:scalafix-rules:${scalafixRulesDependency.revision}")
        .mkString(" ", " ", " --settings.lint.error.includes=.* --check")
      Def.task {
        (Compile / scalafix).toTask(arg).value
      }
    }.value,
    Compile / compile := (Compile / compile).dependsOn((Compile / scalafix).toTask(" MyScalafixRuleRule")).value,
    Compile / compile := (Compile / compile).dependsOn(dogfooding).value,
    scriptedBufferLog := false,
    scriptedLaunchOpts += ("-Dscalafix-rules.version=" + version.value),
    scriptedLaunchOpts += ("-Dscalafix.version=" + _root_.scalafix.sbt.BuildInfo.scalafixVersion),
    sbtTestDirectory := (LocalRootProject / baseDirectory).value / "sbt-test",
    scriptedLaunchOpts ++= {
      import scala.jdk.CollectionConverters.*
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
  libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.14.0",
  libraryDependencies += "com.google.inject" % "guice" % "6.0.0", // scala-steward:off
  libraryDependencies ++= {
    if (scalaBinaryVersion.value != "3") {
      Seq(compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.4" cross CrossVersion.full))
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
  libraryDependencies += "org.mockito" % "mockito-subclass" % "5.20.0",
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
  .aggregate(tests.projectRefs *)
  .settings(
    commonSettings,
    scalaVersion := V.scala212,
    publish / skip := true,
  )

lazy val tests = projectMatrix
  .settings(
    commonSettings,
    publish / skip := true,
    libraryDependencies += "org.scala-lang.modules" % "scala-asm" % "9.9.0-scala-1",
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
    (rulesCrossVersions.map(VirtualAxis.scalaABIVersion) :+ VirtualAxis.jvm) *
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
