import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import play.PlayImport.PlayKeys.playPackageAssets

object BuildSettings {
  val buildOrganization = "deeva"
  val buildVersion = "0.1"
  val buildScalaVersion = "2.11.2"

  val buildSettings = Seq(
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )

  val deevaShellScript = Seq(
    "#!/usr/bin/env sh",
    "set -e",
    """DETECTED_JAVA_HOME=${JAVA_HOME:?"JAVA_HOME environment variable is not set. Make sure you have JDK 1.7+ installed and set the JAVA_HOME environment variable accordingly."}""",
    """TOOLS_JAR="${DETECTED_JAVA_HOME}/lib/tools.jar"""",
    "if [ -e $TOOLS_JAR ]",
    "then",
    """    exec java -jar "$0" "$@"""",
    "else",
    """    echo "Cannot find tools.jar, make sure the Java Development Kit 1.7+ is installed."""",
    "fi"
  )

  val deevaAssemblySettings = assemblySettings ++ Seq(
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
      {
        case x if x.endsWith("ServerWithStop.class") => MergeStrategy.last
        case x if x.endsWith("spring.tooling") => MergeStrategy.last
        case x => old(x)
      }
    },
    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      cp filter {_.data.getName == "tools.jar"}
    },
    assemblyOption in assembly ~= {
      _.copy(prependShellScript = Some(deevaShellScript))
    },
    jarName in assembly := { s"${name.value}-${version.value}" },
    mainClass in assembly := Some("Deeva"),
    fullClasspath in assembly += Attributed.blank(playPackageAssets.value)
  )
}

object Dependencies {
  val gson = "com.google.code.gson" % "gson" % "2.3"
}

object DeevaBuild extends Build {
  import BuildSettings._
  import Dependencies._

  val backendJavaDeps = Seq(gson)

  lazy val deevaJava = (
    Project("deeva-java", file("."), settings = buildSettings ++ deevaAssemblySettings)
      dependsOn(webFrontendJava)
      aggregate(debuggerJava, webFrontend)
      enablePlugins(play.PlayScala)
  )

  lazy val debuggerJava = (
    Project("deeva-java-backend", file("debugger-java"), settings = buildSettings)
      settings(libraryDependencies ++= backendJavaDeps)
  )

  lazy val webFrontend = (
    Project( "deeva-web-frontend", file("webfront-end"), settings = buildSettings)
      enablePlugins(play.PlayScala)
  )

  lazy val webFrontendJava = webFrontend.dependsOn(debuggerJava)
}
