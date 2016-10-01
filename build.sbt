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

val glasciia_core = module("core").settings(
  libraryDependencies ++= Seq(
    "io.reactivex"          % "rxscala_2.11"            % "0.26.2",
    "com.badlogicgames.gdx" %   "gdx"                   % "1.9.4",
    "com.badlogicgames.gdx" %   "gdx-freetype"          % "1.9.4",
    "com.badlogicgames.gdx" %   "gdx-backend-lwjgl"     % "1.9.4",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.4" classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.4" classifier "natives-desktop"
  )
).dependsOn(uri("git://github.com/gigurra/scala-libgurra.git#0.1.3"))

val glasciia = aggregate(glasciia_core)

def module(name: String, dependencies: ClasspathDep[ProjectReference]*): Project = {
  Project(
    id = s"glasciia-$name",
    base = file(s"glasciia-$name"),
    settings = commonSettings,
    dependencies = dependencies
  )
}

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
