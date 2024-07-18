import sbt._
import sbt.Keys._
import sbt.Defaults._
import sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild
import com.evolution.artifactory.ArtifactoryPlugin.autoImport.ResolverOpsArtifactory

object BuildSettings {
  val artifactId = "akka-http-documenteddsl"

  lazy val basicSettings = Seq(
    name                  := artifactId,
    organization          := "com.evolutiongaming",
    homepage              := Some(url("https://github.com/evolution-gaming/akka-http-documenteddsl")),
    startYear             := Some(2016),
    organizationName      := "Evolution",
    organizationHomepage  := Some(url("https://evolution.com")),
    scalaVersion          := crossScalaVersions.value.head,
    crossScalaVersions    := Seq("2.13.3", "2.12.12"),
    releaseCrossBuild     := true,
    publishTo             := Some(Resolver.evolutionReleases),
    licenses              := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))))
}