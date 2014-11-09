name := """deeva-web-frontend"""

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
).map(_.exclude("commons-logging", "commons-logging"))
