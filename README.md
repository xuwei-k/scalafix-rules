# scalafix rules

[![maven](https://img.shields.io/maven-central/v/com.github.xuwei-k/scalafix-rules_2.13)](https://search.maven.org/artifact/com.github.xuwei-k/scalafix-rules_2.13)
[![scaladoc](https://javadoc.io/badge2/com.github.xuwei-k/scalafix-rules_2.13/javadoc.svg)](https://javadoc.io/doc/com.github.xuwei-k/scalafix-rules_2.13/latest/fix/index.html)


| scalafix | scalafix-rules |
|----------|----------------|
| 0.14.x   | 0.6.x          |
| 0.13.x   | 0.5.x          |
| 0.12.x   | 0.4.x          |

`project/plugins.sbt`

```scala
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "<version>")
```

`build.sbt`

```scala
ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.6.15"
```

or

```
sbt > scalafixAll dependency:SomeRuleName@com.github.xuwei-k:scalafix-rules:version
```

or

```
sbt > scalafixAll https://raw.githubusercontent.com/xuwei-k/scalafix-rules/main/rules/src/main/scala/fix/SomeRuleName.scala
```
