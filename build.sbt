import sbt._
import sbt.Keys._
import sbt.Defaults._
import BuildSettings._
import Dependencies._
import Examples._

lazy val project = (Project(artifactId, file("."))
  configs ExamplesConfig
  settings inConfig(ExamplesConfig)(compileBase ++ compileSettings ++ Seq(
  run <<= Defaults.runTask(fullClasspath in ExamplesConfig, mainClass in run, runner in run),
  runMain <<= Defaults.runMainTask(fullClasspath in ExamplesConfig, runner in run)))
  settings buildSettings
  settings Seq(
    resolvers += Resolver.bintrayRepo("evolutiongaming", "maven"),
    resolvers += Resolver.bintrayRepo("hseeberger", "maven"),
    libraryDependencies ++= Seq(
      akkaHttpCore, akkaHttp, akkaHttpTestKit,
      akkaHttpPlayJson, jsonSchema,
      scalaTest, mockito)))