import sbt._

object Dependencies {
  val AkkaHttpVersion = "10.2.10" // `10.2.10` is last open source version before switch to BSL
  val AkkaVersion = "2.6.21" // `2.6.21` is last open source version before switch to BSL
  val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
  val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
  val akkaHttpPlayJson = "com.evolutiongaming" %% "akka-http-play-json" % "0.3.0"
  val jsonSchema = "com.evolutiongaming" %% "autoschema" % "2.0.0"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.20" % Test
  val mockito = "org.mockito" % "mockito-core" % "5.23.0" % Test
}
