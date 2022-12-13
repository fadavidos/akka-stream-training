name := "AkkaStreamTraining"

version := "0.1"

scalaVersion := "2.13.8"

idePackagePrefix := Some("com.fadavidos.akka.streams")

val akkaVersion = "2.7.0"

libraryDependencies ++=List(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)


