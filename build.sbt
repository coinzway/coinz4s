import org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings

name := "coinz4s"

version := "0.1"

ThisBuild / scalaVersion := "2.13.1"

organization := "coinzway"

licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))

lazy val IntegrationTest = config("it") extend Test

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings)
  )
  .aggregate(
    core,
    bitcoind,
    litecoind,
    bitcoindCash,
    dogecoind,
    dashd,
    zcashd
  )

val sttpVersion = "1.7.2"
val akkaVersion = "2.6.3"
val scalaTestVersion = "3.1.0"
val sprayJsonVersion = "1.3.5"
val pureConfigVersion = "0.12.2"

lazy val dependencies = {
  Seq(
    "org.scalatest"         %% "scalatest"         % scalaTestVersion % "test,it",
    "io.spray"              %% "spray-json"        % sprayJsonVersion,
    "com.softwaremill.sttp" %% "core"              % sttpVersion,
    "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion,
    "com.typesafe.akka"     %% "akka-stream"       % akkaVersion % "provided,test,it"
  )
}

lazy val testUtilDependencies = {
  Seq("com.github.pureconfig" %% "pureconfig" % pureConfigVersion)
}

addCommandAlias("testAll", ";test;it:test")
addCommandAlias("formatAll", ";scalafmtAll;test:scalafmtAll;it:scalafmtAll;scalafmtSbt")
addCommandAlias("compileAll", ";compile;test:compile;it:compile")
addCommandAlias("checkFormatAll", ";scalafmtCheckAll;scalafmtSbtCheck;it:scalafmtCheckAll")

lazy val testUtils = (project in file("testUtils"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies ++ testUtilDependencies
  )
  .dependsOn(core)

lazy val core = (project in file("core"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )

lazy val bitcoind = (project in file("bitcoind"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(testUtils)

lazy val litecoind = (project in file("litecoind"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)
  .dependsOn(testUtils)

lazy val bitcoindCash = (project in file("bitcoindCash"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)
  .dependsOn(testUtils)

lazy val dogecoind = (project in file("dogecoind"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)
  .dependsOn(testUtils)

lazy val dashd = (project in file("dashd"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)
  .dependsOn(testUtils)

lazy val zcashd = (project in file("zcashd"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)
  .dependsOn(testUtils)
