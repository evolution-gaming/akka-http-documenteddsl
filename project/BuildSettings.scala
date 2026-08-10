import com.evolution.artifactory.ArtifactoryPlugin.autoImport.ResolverOpsArtifactory
import sbt.*
import sbt.Defaults.*
import sbt.Keys.*

object BuildSettings {
  val artifactId = "akka-http-documenteddsl"

  lazy val basicSettings = Seq(
    name := artifactId,
    organization := "com.evolutiongaming",
    homepage := Some(uri("https://github.com/evolution-gaming/akka-http-documenteddsl")),
    startYear := Some(2016),
    organizationName := "Evolution",
    organizationHomepage := Some(uri("https://evolution.com")),
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := Seq("2.13.18"),
    publishTo := Some(Resolver.evolutionReleases),
    licenses := Seq(License("Apache-2.0", uri("http://www.apache.org/licenses/LICENSE-2.0"))),
  )
}