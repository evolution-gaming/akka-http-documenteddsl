import sbt._
import sbt.Keys._
import sbt.Defaults._
import BuildSettings._
import Dependencies._
import Examples._

val alias: Seq[sbt.Def.Setting[_]] =
  //  addCommandAlias("check", "all versionPolicyCheck Compile/doc") ++
  addCommandAlias("check", "show version") ++
    addCommandAlias("build", "+all compile test")

lazy val project = (Project(artifactId, file("."))
  .configs(ExamplesConfig)
  .settings (
    inConfig(ExamplesConfig)(compileBase ++ compileSettings ++ Seq(
      run := Defaults.runTask(ExamplesConfig / fullClasspath , run / mainClass , run / runner).evaluated,
      runMain := Defaults.runMainTask(ExamplesConfig / fullClasspath, run / runner).evaluated)
    ),
  )
  .settings(alias)
  .settings(basicSettings)
  .settings(
    publishTo := Some(Resolver.evolutionReleases)
  )
  .settings(Seq(
    libraryDependencies ++= Seq(
      akkaHttpCore,
      akkaHttp,
      akkaHttpTestKit,
      akkaHttpPlayJson,
      akkaStream,
      akkaStreamTestkit,
      jsonSchema,
      scalaTest,
      mockito))),
  )