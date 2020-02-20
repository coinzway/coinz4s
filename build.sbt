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
    dogecoind
  )

lazy val dependencies = {
  val sttpVersion = "1.7.2"
  val akkaVersion = "2.6.3"
  val scalaTestVersion = "3.1.0"
  val sprayJsonVersion = "1.3.5"

  Seq(
    "org.scalatest"         %% "scalatest"         % scalaTestVersion % "test,it",
    "io.spray"              %% "spray-json"        % sprayJsonVersion,
    "com.softwaremill.sttp" %% "core"              % sttpVersion,
    "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion,
    "com.typesafe.akka"     %% "akka-stream"       % akkaVersion % "provided,test,it"
  )
}

addCommandAlias("testAll", ";test;it:test")
addCommandAlias("formatAll", ";scalafmtAll;test:scalafmtAll;scalafmtSbt")
addCommandAlias("compileAll", ";compile;test:compile;it:compile")
addCommandAlias("checkFormatAll", ";scalafmtCheckAll;scalafmtSbtCheck")

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

lazy val litecoind = (project in file("litecoind"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)

lazy val bitcoindCash = (project in file("bitcoindCash"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)

lazy val dogecoind = (project in file("dogecoind"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .dependsOn(core)
  .dependsOn(bitcoind)
