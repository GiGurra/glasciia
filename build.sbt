lazy val commonSettings = Seq(
  organization := "com.github.gigurra",
  version := "SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
)

lazy val glasciia_core = module("core").settings(
  libraryDependencies ++= Seq(
    "com.github.gigurra"    %%  "libgurra"              % "0.2.6",
    "io.reactivex"          %%  "rxscala"               % "0.26.2",
    "com.badlogicgames.gdx" %   "gdx"                   % "1.9.4",
    "com.badlogicgames.gdx" %   "gdx-box2d"             % "1.9.4",
    "com.badlogicgames.gdx" %   "gdx-freetype"          % "1.9.4"
  )
)

lazy val glasciia_desktop = module("desktop", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-lwjgl"     % "1.9.4",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.4" classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.4" classifier "natives-desktop"
  )
)

lazy val glasciia_ios = module("ios", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.4" classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.4" classifier "natives-desktop"
  )
)

lazy val glasciia_android = module("android", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.4" classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.4" classifier "natives-desktop"
  )
)

lazy val glasciia_test1 = module("test1", dependencies = glasciia_core, glasciia_desktop).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-tools"             % "1.9.4"
  )
)

lazy val glasciia = aggregate(
  glasciia_core,
  glasciia_desktop,
  glasciia_ios,
  glasciia_android,
  glasciia_test1
)

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

