val versions = new {

  // Platform
  val scala     = "2.11.8"
  val java      = "1.6"

  // This project
  val project   = "0.10.1-SNAPSHOT"

  // Dependencies
  val libgurra  = "0.5.2-SNAPSHOT"
  val libgdx    = "1.9.6"
  val scalatest = "2.2.4"
  val mockito   = "1.10.19"
}

val commonSettings = Seq(
  organization := "com.github.gigurra",
  version := versions.project,
  scalaVersion := versions.scala,
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
  scalacOptions += s"-target:jvm-${versions.java}",
  javacOptions ++= Seq("-source", versions.java, "-target", versions.java),
  pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray),
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

lazy val glasciia_core = module("core").settings(
  libraryDependencies ++= Seq(
    "com.github.gigurra"    %%  "libgurra"              % versions.libgurra,
    "com.badlogicgames.gdx" %   "gdx"                   % versions.libgdx,
    "com.badlogicgames.gdx" %   "gdx-freetype"          % versions.libgdx,
    "org.scalatest"         %% "scalatest"              % versions.scalatest  % "test",
    "org.mockito"           %  "mockito-core"           % versions.mockito    % "test"
  )
)

lazy val glasciia_desktop = module("desktop", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-lwjgl"     % versions.libgdx,
    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-desktop",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-desktop"
  )
)

lazy val glasciia_ios = module("ios", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-robovm"    % versions.libgdx,
    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-ios",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-ios"
  )
)

lazy val glasciia_android = module("android", dependencies = glasciia_core).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-backend-android"   % versions.libgdx,

    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-armeabi",
    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-armeabi-v7a",
    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-arm64-v8a",
    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-x86",
    "com.badlogicgames.gdx" %   "gdx-platform"          % versions.libgdx classifier "natives-x86_64",

    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-armeabi",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-armeabi-v7a",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-arm64-v8a",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-x86",
    "com.badlogicgames.gdx" %   "gdx-freetype-platform" % versions.libgdx classifier "natives-x86_64"
  )
)

lazy val glasciia_test1 = module("test1", dependencies = glasciia_core, glasciia_desktop).settings(
  libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" %   "gdx-tools"             % versions.libgdx
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

