lazy val commonSettings = Seq(
  organization := "se.gigurra",
  version := "SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
  libraryDependencies ++= Seq(
    "org.scalatest"        %%   "scalatest"             %   "2.2.4"     %   "test",
    "org.mockito"           %   "mockito-core"          %   "1.10.19"   %   "test"
  )
)

lazy val glasciia_core = Project(
  id = "glasciia-core",
  base = file("glasciia-core"),
  settings = commonSettings
)


lazy val glasciia = Project(id = "glasciia", base = file("."), settings = commonSettings).dependsOn(
  glasciia_core
).aggregate(
  glasciia_core
)
