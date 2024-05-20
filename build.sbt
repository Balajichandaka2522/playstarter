scalaVersion := "2.12.10"  // Scala version compatible with Play Framework 2.7.x

javacOptions ++= Seq(
  "-source", "1.8",   // Java source compatibility
  "-target", "1.8"    // Java target compatibility
)

libraryDependencies += guice

// Specify the Play Framework version compatible with Java 8
libraryDependencies += "com.typesafe.play" %% "play-guice" % "2.7.9"
