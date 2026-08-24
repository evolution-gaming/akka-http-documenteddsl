import BuildSettings.*
import Dependencies.*
import Examples.*
import sbt.*
import sbt.Defaults.*
import sbt.Keys.*

val alias: Seq[sbt.Def.Setting[?]] =
  addCommandAlias("check", "all scalafmtCheckRepo versionPolicyCheck Compile/doc") ++
    addCommandAlias("fmt", "scalafmtRepo") ++
    addCommandAlias("build", "+all compile testFull")

lazy val project = Project(artifactId, file("."))
  .configs(ExamplesConfig)
  .settings(
    inConfig(ExamplesConfig)(compileBase ++ compileSettings ++ Seq(
      run := Defaults.runTask(ExamplesConfig / fullClasspath, run / mainClass, run / runner).evaluated,
      runMain := Defaults.runMainTask(ExamplesConfig / fullClasspath, run / runner).evaluated,
    )),
  )
  .settings(alias)
  .settings(basicSettings)
  .settings(
    publishTo := Some(Resolver.evolutionReleases),
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
      mockito,
    ),
  ))
