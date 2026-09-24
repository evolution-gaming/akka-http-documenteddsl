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

lazy val root = Project("root", file("."))
  .aggregate(`http-documenteddsl`.projectRefs *)
  .settings(alias)
  .settings(basicSettings)
  .settings(
    scalaVersion := BuildSettings.scalaVersions.head,
    Compile / sources := Nil,
    Test / sources := Nil,
    publish / skip := true,
    versionPolicyCheck / skip := true,
  )

/**
 * `akka-http-documenteddsl` and `pekko-http-documenteddsl`. The pekko row compiles `src` with
 * `akka` rewritten to `org.apache.pekko` by [[PekkoPort]]. Edit `src`, never the generated tree.
 */
lazy val `http-documenteddsl` = projectMatrix
  .in(file("."))
  .settings(basicSettings)
  .settings(libraryDependencies ++= Seq(jsonSchema, scalaTest, mockito))
  .defaultAxes(VirtualAxis.jvm, VirtualAxis.scalaABIVersion(BuildSettings.scalaVersions.head))
  .customRow(
    scalaVersions = BuildSettings.scalaVersions,
    axisValues = Seq(HttpAxis.akka, VirtualAxis.jvm),
    process = _
      .configs(ExamplesConfig)
      .settings(
        inConfig(ExamplesConfig)(compileBase ++ compileSettings ++ Seq(
          run := Defaults.runTask(ExamplesConfig / fullClasspath, run / mainClass, run / runner).evaluated,
          runMain := Defaults.runMainTask(ExamplesConfig / fullClasspath, run / runner).evaluated,
        )),
        name := "akka-http-documenteddsl",
        libraryDependencies ++= Seq(
          akkaHttpCore,
          akkaHttp,
          akkaHttpTestKit,
          akkaHttpPlayJson,
          akkaStream,
          akkaStreamTestkit,
        ),
      ),
  )
  .customRow(
    scalaVersions = BuildSettings.scalaVersions,
    axisValues = Seq(HttpAxis.pekko, VirtualAxis.jvm),
    settings = Seq(
      name := "pekko-http-documenteddsl",
      versionPolicyCheck / skip := true,
      Compile / unmanagedSourceDirectories -= (Compile / scalaSource).value,
      Test / unmanagedSourceDirectories -= (Test / scalaSource).value,
      Compile / sourceGenerators += Def.task {
        PekkoPort.port((Compile / scalaSource).value, (Compile / sourceManaged).value)
      }.taskValue,
      Test / sourceGenerators += Def.task {
        PekkoPort.port((Test / scalaSource).value, (Test / sourceManaged).value)
      }.taskValue,
      libraryDependencies ++= Seq(
        pekkoHttpCore,
        pekkoHttp,
        pekkoHttpTestKit,
        pekkoHttpPlayJson,
        pekkoStream,
        pekkoStreamTestkit,
      ),
    ),
  )
