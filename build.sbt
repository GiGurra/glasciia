lazy val commonSettings = Seq(
  organization := "com.github.gigurra",
  version := "0.4.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
  scalacOptions += "-target:jvm-1.6",
  javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
  pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray),
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

lazy val glasciia_core = module("core").settings(
  libraryDependencies ++= Seq(
    "com.github.gigurra"    %%  "libgurra"              % "0.3.6-SNAPSHOT",
    "com.badlogicgames.gdx" %   "gdx"                   % "1.9.5",
    "com.badlogicgames.gdx" %   "gdx-freetype"          % "1.9.5"
  )
)

lazy val glasciia_desktop = module("desktop", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-lwjgl"     % "1.9.5",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-desktop"
  )
)

lazy val glasciia_ios = module("ios", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-robovm"    % "1.9.5",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-ios",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-ios"
  )
)

lazy val glasciia_android = module("android", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-android"   % "1.9.5",

    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-armeabi",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-armeabi-v7a",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-arm64-v8a",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-x86",
    "com.badlogicgames.gdx" %   "gdx-platform"          % "1.9.5" classifier "natives-x86_64",

    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-armeabi",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-armeabi-v7a",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-arm64-v8a",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-x86",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % "1.9.5" classifier "natives-x86_64"
  )
)

lazy val glasciia_test1 = module("test1", dependencies = glasciia_core, glasciia_desktop).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-tools"             % "1.9.5"
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

