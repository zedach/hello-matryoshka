

name := "Hello world matryoshka"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.4"

libraryDependencies +=  "com.slamdata" %% "matryoshka-core" % "0.21.3"

fork in run := true

connectInput in run := true

outputStrategy := Some(StdoutOutput)