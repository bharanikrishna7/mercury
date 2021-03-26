name := "polygot"
version := "0.1.0"
scalaVersion := "2.13.5"
organization := "net.chekuri"

developers := List(
  Developer(
    "bharanikrishna7",
    "Venkata Bharani Krishna Chekuri",
    "bharanikrishna7@gmail.com",
    url("https://github.com/bharanikrishna7/")
  )
)

val scala_logging_version = "3.9.3"
val logback_classic_version = "1.2.3"
val scalatest_version = "3.2.6"
val mysql_version = "8.0.23"

val scala_logging_library = "com.typesafe.scala-logging" %% "scala-logging" % scala_logging_version
val logback_classic_library = "ch.qos.logback" % "logback-classic" % logback_classic_version
val scalactic_library = "org.scalactic" %% "scalactic" % scalatest_version
val scalatest_library = "org.scalatest" %% "scalatest" % scalatest_version % "test"
val mysql_library = "mysql" % "mysql-connector-java" % mysql_version

val artima_resolver = ("Artima Maven Repository" at "http://repo.artima.com/releases").withAllowInsecureProtocol(true)

val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      scala_logging_library,
      logback_classic_library,
      scalactic_library,
      scalatest_library,
      mysql_library
    ),
    resolvers ++= Seq(
      artima_resolver
    ),
    scalafmtOnCompile := true,
    parallelExecution in Test := false
  )