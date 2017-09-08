import sbt._

object Dependencies {
  val AkkaHttpVersion = "10.0.10"
  val AkkaVersion = "2.5.3"
  lazy val akkaHttpCore      = "com.typesafe.akka"   %% "akka-http-core"       % AkkaHttpVersion
  lazy val akkaHttp          = "com.typesafe.akka"   %% "akka-http"            % AkkaHttpVersion
  lazy val akkaHttpTestKit   = "com.typesafe.akka"   %% "akka-http-testkit"    % AkkaHttpVersion % Test
  lazy val akkaStream        = "com.typesafe.akka"   %% "akka-stream"          % AkkaVersion
  lazy val akkaStreamTestkit = "com.typesafe.akka"   %% "akka-stream-testkit"  % AkkaVersion % Test
  lazy val akkaHttpPlayJson  = "com.evolutiongaming" %% "akka-http-play-json"  % "0.1.10"
  lazy val jsonSchema        = "com.sauldhernandez"  %% "autoschema"           % "1.0.4"
  lazy val scalaTest         = "org.scalatest"       %% "scalatest"            % "3.0.3" % Test
  lazy val mockito           = "org.mockito"          % "mockito-core"         % "2.2.29" % Test
}