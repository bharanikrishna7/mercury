name := "mercury"
version := "0.1.0"
scalaVersion := "2.13.3"
organization := "net.chekuri"

developers := List(
  Developer(
    "bharanikrishna7",
    "Venkata Bharani Krishna Chekuri",
    "bharanikrishna7@gmail.com",
    url("https://github.com/bharanikrishna7/")
  )
)

val airframe_log_version: String = "20.10.3"
val airframe_log_library: ModuleID = "org.wvlet.airframe" %% "airframe-log" % airframe_log_version

val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(airframe_log_library),
    scalafmtOnCompile := true
  )