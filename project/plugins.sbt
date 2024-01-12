externalResolvers += Resolver.bintrayIvyRepo("evolutiongaming", "sbt-plugins")

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.6.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.7.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.3.0")

addSbtPlugin("com.evolutiongaming" % "sbt-scalac-opts-plugin" % "0.0.5")