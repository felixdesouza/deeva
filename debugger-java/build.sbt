name := """debugger-java"""

lazy val toolsJarLocation = taskKey[File]("Finds the tools.jar with respect to the current operating system.")

toolsJarLocation := {
  val toolsJarFile = file(sys.props("java.home")) / ".." / "lib" / "tools.jar"
  if (toolsJarFile.exists) {
    toolsJarFile
  } else {
    sys.error(s"Cannot find tools.jar as this file doesn't exist: $toolsJarFile \nFor Java 1.7+ tools.jar is located " +
      s"within <JDK_PATH>/lib/ and requires the JDK to be installed.")
  }
}

unmanagedJars in Compile += toolsJarLocation.value
