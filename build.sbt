import sbt._
import sbt.Keys._
import sbt.Defaults._
import BuildSettings._
import Dependencies._
import Examples._

resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-http-core"       % AkkaHttpVersion,
  "com.typesafe.akka"   %% "akka-http"            % AkkaHttpVersion,
  "com.typesafe.akka"   %% "akka-http-testkit"    % AkkaHttpVersion,
  "de.heikoseeberger"   %% "akka-http-play-json"  % "1.10.1",
  "com.sauldhernandez"  %% "autoschema"           % "1.0.3",
  "org.scalatest"       %% "scalatest"            % "3.0.1" % Test,
  "org.mockito"          % "mockito-core"         % "2.2.29" % Test)

lazy val project = (Project(artifactId, file("."))
  configs ExamplesConfig
  settings inConfig(ExamplesConfig)(compileBase ++ compileSettings ++ Seq(
  run     <<= Defaults.runTask(fullClasspath in ExamplesConfig, mainClass in run, runner in run),
  runMain <<= Defaults.runMainTask(fullClasspath in ExamplesConfig, runner in run)))
  settings buildSettings)