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

  val PekkoHttpVersion = "1.4.0"
  val PekkoVersion = "1.7.0"
  val pekkoHttpCore = "org.apache.pekko" %% "pekko-http-core" % PekkoHttpVersion
  val pekkoHttp = "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion
  val pekkoHttpTestKit = "org.apache.pekko" %% "pekko-http-testkit" % PekkoHttpVersion % Test
  val pekkoStream = "org.apache.pekko" %% "pekko-stream" % PekkoVersion
  val pekkoStreamTestkit = "org.apache.pekko" %% "pekko-stream-testkit" % PekkoVersion % Test
  val pekkoHttpPlayJson = "com.evolutiongaming" %% "pekko-http-play-json" % "0.3.0"

  val JacksonVersion = "2.22.3"
  val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % JacksonVersion
  val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion

  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.20" % Test
  val mockito = "org.mockito" % "mockito-core" % "5.24.0" % Test
}
