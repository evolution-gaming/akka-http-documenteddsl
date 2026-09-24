import com.evolution.artifactory.ArtifactoryPlugin.autoImport.ResolverOpsArtifactory
import com.typesafe.tools.mima.core.*
import sbt.*
import sbt.Defaults.*
import sbt.Keys.*
import sbtversionpolicy.SbtVersionPolicyPlugin.autoImport.*

object BuildSettings {
  val scalaVersions: Seq[String] = Seq("2.13.18")

  lazy val basicSettings = Seq(
    organization := "com.evolutiongaming",
    homepage := Some(uri("https://github.com/evolution-gaming/akka-http-documenteddsl")),
    startYear := Some(2016),
    organizationName := "Evolution",
    organizationHomepage := Some(uri("https://evolution.com")),
    publishTo := Some(Resolver.evolutionReleases),
    licenses := Seq(License("Apache-2.0", uri("http://www.apache.org/licenses/LICENSE-2.0"))),
    versionPolicyIntention := Compatibility.BinaryCompatible,
  )
}
