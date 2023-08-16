package app.logorrr

import app.logorrr.util.OsUtil

object LogoRRRAppLauncher {

  /** launcher for macos installer to circumvent module loading mechanism by javafx (dirty hack) */
  def main(args: Array[String]): Unit = {

    if (OsUtil.isMac) {
      System.loadLibrary("LogoRRRSwift")
      System.loadLibrary("LogoRRR")
    }

    LogoRRRApp.main(args)
  }

  // private def test(): Unit = {
  // OsxBridge.printHelloWorldImpl()
  // OsxBridge.registerPath(Paths.get("/Users/lad/gh/LogoRRR/app/target/app-23.2.0-SNAPSHOT.jar").toAbsolutePath.toString)
  // OsxBridge.releasePath(Paths.get("/Users/lad/gh/LogoRRR/app/target/app-23.2.0-SNAPSHOT.jar").toAbsolutePath.toString)
  //}
}
