package app.logorrr

import app.logorrr.util.OsUtil

object LogoRRRAppLauncher {

  /** launcher for macos installer to circumvent module loading mechanism by javafx (dirty hack) */
  def main(args: Array[String]): Unit = {

    loadNativeLibraries()

    LogoRRRApp.main(args)
  }


  def loadNativeLibraries(): Unit = {
    if (OsUtil.isMac) {
      System.loadLibrary("LogoRRRSwift")
      System.loadLibrary("LogoRRR")
    }
  }
}
