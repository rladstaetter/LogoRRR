package app.logorrr

import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil


object LogoRRRNative extends TinyLog:

  def loadNativeLibraries(): Unit =
    if OsUtil.enableSecurityBookmarks then
      val nativeLogoRRRSwift = "LogoRRRSwift"
      val nativeLogoRRR = "LogoRRR"
      System.loadLibrary(nativeLogoRRRSwift)
      System.loadLibrary(nativeLogoRRR)
      logTrace(s"Loaded native libraries '$nativeLogoRRRSwift' and '$nativeLogoRRR'.")

