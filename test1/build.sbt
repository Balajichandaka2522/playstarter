name := """test1"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

//libraryDependencies += guice
// https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver
libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.14"
libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "3.12.0",
  "dev.morphia.morphia" % "core" % "1.5.8",
  "org.easytesting" % "fest-assert" % "1.4" % "test"
)

