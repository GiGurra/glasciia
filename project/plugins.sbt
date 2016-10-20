resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

