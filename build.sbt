val libgdxVersion = "1.9.4"

val commonSettings = Seq(
  organization := "se.gigurra",
  version := "SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
  libraryDependencies ++= Seq(
    "org.scalatest"        %%   "scalatest"             %   "2.2.4"     %   "test",
    "org.mockito"           %   "mockito-core"          %   "1.10.19"   %   "test"
  )
)

val glasciia_core = Project(
  id = "glasciia-core",
  base = file("glasciia-core"),
  settings = commonSettings
)

val glasciia_gdx = Project(
  id = "glasciia-gdx",
  base = file("glasciia-gdx"),
  settings = commonSettings,
  dependencies = Seq(glasciia_core)
).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx"                   % libgdxVersion,
    "com.badlogicgames.gdx" %   "gdx-freetype"          % libgdxVersion,
    "com.badlogicgames.gdx" %   "gdx-backend-lwjgl"     % libgdxVersion,
    "com.badlogicgames.gdx" %   "gdx-platform"          % libgdxVersion classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % libgdxVersion classifier "natives-desktop"
  )
)

val glasciia = aggregate(glasciia_core, glasciia_gdx)

def aggregate(projects: Project*): Project = {
  def toDependency(p: Project): ClasspathDep[ProjectReference] = p
  def toReference(p: Project): ProjectReference = p
  Project(
    id = "glasciia",
    base = file("."),
    settings = commonSettings,
    dependencies = projects.map(toDependency)
  ).aggregate(
    projects.map(toReference):_*
  )
}
