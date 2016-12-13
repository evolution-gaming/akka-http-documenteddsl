import java.net.URL
import sbt._
import sbt.Keys._
import bintray.BintrayPlugin.autoImport.bintrayOrganization

object BuildSettings {
  val artifactId = "akka-http-autodoc"

  lazy val buildSettings = Seq(
    name                  := artifactId,
    organization          := "com.evolutiongaming",
    homepage              := Some(new URL("http://github.com/evolution-gaming/akka-http-autodoc")),
    startYear             := Some(2016),
    organizationName      := "Evolution Gaming",
    organizationHomepage  := Some(url("http://evolutiongaming.com")),
    bintrayOrganization   := Some("evolutiongaming"),
    scalaVersion          := "2.11.8",
    licenses              := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
    scalacOptions         ++= Seq(
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture"))
}