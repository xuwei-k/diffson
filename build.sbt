val scala212 = "2.12.16"
val scala213 = "2.13.8"
val scala3 = "3.1.3"

val scalatestVersion = "3.2.13"
val scalacheckVersion = "1.17.0"

ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := Seq(scala212, scala213, scala3)

ThisBuild / tlBaseVersion := "4.1"

ThisBuild / organization := "org.gnieh"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  tlGitHubDev("satabin", "Lucas Satabin")
)

lazy val commonSettings = Seq(
  description := "Json diff/patch library",
  homepage := Some(url("https://github.com/gnieh/diffson"))
)

lazy val diffson = tlCrossRootProject.aggregate(core, sprayJson, circe, playJson, testkit)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .enablePlugins(ScalaUnidocPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "diffson-core",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %%% "scala-collection-compat" % "2.8.1",
      "org.typelevel" %%% "cats-core" % "2.8.0",
      "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
      "org.scalacheck" %%% "scalacheck" % scalacheckVersion % Test
    )
  )

lazy val testkit = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("testkit"))
  .settings(commonSettings: _*)
  .settings(name := "diffson-testkit",
            libraryDependencies ++= Seq("org.scalatest" %%% "scalatest" % scalatestVersion,
                                        "org.scalacheck" %%% "scalacheck" % scalacheckVersion))
  .dependsOn(core)

lazy val sprayJson = project
  .in(file("sprayJson"))
  .settings(commonSettings: _*)
  .settings(name := "diffson-spray-json",
            crossScalaVersions := Seq(scala212, scala213),
            libraryDependencies += "io.spray" %% "spray-json" % "1.3.6")
  .dependsOn(core.jvm, testkit.jvm % Test)

lazy val playJson = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("playJson"))
  .settings(commonSettings: _*)
  .settings(name := "diffson-play-json",
            libraryDependencies += "com.typesafe.play" %%% "play-json" % "2.9.3",
            crossScalaVersions := Seq(scala212, scala213))
  .dependsOn(core, testkit % Test)

val circeVersion = "0.14.3"
lazy val circe = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("circe"))
  .settings(commonSettings: _*)
  .settings(
    name := "diffson-circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  )
  .dependsOn(core, testkit % Test)
