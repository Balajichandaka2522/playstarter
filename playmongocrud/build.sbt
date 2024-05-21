name := """playmongocrud"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.0"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.3.4"
libraryDependencies += guice

