name := "ArnoldC-Native"

version := "1.0.0-NATIVE"

scalaVersion := "2.13.12"

// Dependencies
libraryDependencies ++= Seq(
  "org.parboiled" %% "parboiled-scala" % "1.4.1",
  "org.ow2.asm" % "asm" % "9.6",
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)

// Compiler options
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)

// Assembly settings for creating fat JAR
assembly / assemblyJarName := "ArnoldC-Native.jar"
assembly / mainClass := Some("org.arnoldc.ArnoldC")

// Merge strategy for assembly
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
