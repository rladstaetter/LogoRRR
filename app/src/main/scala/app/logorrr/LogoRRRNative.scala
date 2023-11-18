package app.logorrr

import app.logorrr.util.{CanLog, OsUtil}

object LogoRRRNative extends CanLog {

  def loadNativeLibraries(): Unit = {
    if (OsUtil.enableSecurityBookmarks) {
      val nativeLogoRRRSwift = "LogoRRRSwift"
      val nativeLogoRRR = "LogoRRR"
      System.loadLibrary(nativeLogoRRRSwift)
      System.loadLibrary(nativeLogoRRR)
      logTrace(s"Loaded native libraries '$nativeLogoRRRSwift' and '$nativeLogoRRR'.")
    }
  }

}
