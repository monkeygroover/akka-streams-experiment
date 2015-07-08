name := "akka-streams-experiment"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += Resolver.bintrayRepo("mfglabs", "maven")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "com.mfglabs" %% "akka-stream-extensions" % "0.7.1",
  "com.mfglabs" %% "akka-stream-extensions-shapeless" % "0.7.1",
  "io.spray" %%  "spray-json" % "1.3.2",
  //"org.ensime" %% "spray-json-shapeless" % "0.9.10-SNAPSHOT"
  "org.leachbj" % "akka-mqlight-remote" % "0.0.1-SNAPSHOT"
)