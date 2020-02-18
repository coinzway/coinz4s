import org.scalafmt.sbt.ScalafmtPlugin._

name := "coinz4s"

version := "0.1"

scalaVersion := "2.13.1"

organization := "coinzway"

scapegoatVersion in ThisBuild := "1.4.1"

val dependencies = {
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

licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))

lazy val IntegrationTest = config("it") extend Test

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings),
    libraryDependencies ++= dependencies
  )
  .aggregate(
    core,
    btc,
    ltc,
    bch
  )

addCommandAlias("testAll", ";test;it:test")
addCommandAlias("formatAll", ";scalafmtAll;test:scalafmtAll;scalafmtSbt")
addCommandAlias("compileAll", ";compile;test:compile;it:compile")
addCommandAlias("checkFormatAll", ";scalafmtCheckAll;scalafmtSbtCheck")

lazy val core = (project in file("core")).settings(libraryDependencies ++= dependencies)
lazy val btc = (project in file("btc")).settings(libraryDependencies ++= dependencies).dependsOn(core)
lazy val ltc = (project in file("ltc")).settings(libraryDependencies ++= dependencies).dependsOn(core)
lazy val bch = (project in file("bch")).settings(libraryDependencies ++= dependencies).dependsOn(core)
