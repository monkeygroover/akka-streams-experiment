name := "akka-streams-experiment"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += Resolver.bintrayRepo("mfglabs", "maven")

libraryDependencies ++= Seq(
  "com.mfglabs" %% "akka-stream-extensions" % "0.7.1",
  "com.mfglabs" %% "akka-stream-extensions-shapeless" % "0.7.1"
)