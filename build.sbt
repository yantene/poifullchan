import AssemblyKeys._

lazy val buildSettings = Seq(
  name := "poifullchan",
  version := "0.9",
  organization := "net.yantene.poifullchan",
  scalaVersion := "2.10.3"
)

val app = (project in file("app")).
  settings(buildSettings: _*).
  settings(assemblySettings: _*).
  settings(
  )

jarName in assembly := "poifullchan.jar"

mainClass in assembly := Some("net.yantene.poifullchan.Poifullchan")
