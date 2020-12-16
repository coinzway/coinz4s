ThisBuild / sonatypeProfileName := "com.coinzway"

ThisBuild / publishMavenStyle := true

ThisBuild / licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))

ThisBuild / homepage := Some(url("https://github.com/coinzway/coinz4s/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/coinzway/coinz4s/"),
    "scm:git@github.com:coinzway/coinz4s.git"
  )
)

ThisBuild / developers := List(
  Developer(id = "wlk", name = "Wojciech Langiewicz", email = "", url = url("https://www.wlangiewicz.com"))
)
