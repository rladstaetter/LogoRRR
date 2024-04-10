package app.logorrr

import app.logorrr
import app.logorrr.conf.{LogoRRRGlobals, SettingsIO}
import app.logorrr.io.FilePaths
import app.logorrr.meta.AppMeta
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.NativeOpenFileService
import app.logorrr.services.hostservices.{MacNativeHostService, NativeHostServices}
import app.logorrr.util.{CanLog, JfxUtils, OsUtil}
import app.logorrr.views.main.{LogoRRRMain, LogoRRRStage}
import javafx.application.Application
import javafx.stage.Stage

import java.nio.file.Paths

/**
 * Main starting point for LogoRRR Application
 */
// have fun and thanks for reading the code!
object LogoRRRApp extends CanLog {

  /** LogoRRRs own log formatting string */
  val LogFormat = """[%1$tF %1$tT.%1$tN] %3$-40s %4$-13s %5$s %6$s %n"""

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

  def start(stage: Stage
            , services: LogoRRRServices): LogoRRRMain = {
    System.setProperty("user.language", "en")
    System.setProperty("java.util.logging.SimpleFormatter.format", LogFormat)
    LogoRRRNative.loadNativeLibraries()
    // make sure to set css before anything is initialized otherwise the rules won't apply
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    logInfo(s"Started ${AppMeta.fullAppNameWithVersion} in '${Paths.get("").toAbsolutePath.toString}'")

    LogoRRRGlobals.set(services.settings, services.hostServices)
    val logoRRRMain = new LogoRRRMain(JfxUtils.closeStage(stage), services.fileOpenService, services.isUnderTest)
    LogoRRRStage.init(stage, logoRRRMain)
    logoRRRMain.initLogFilesFromConfig()
    LogoRRRStage.show(stage, logoRRRMain)
    logoRRRMain
  }
}


class LogoRRRApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    val hostServices = {
      if (OsUtil.isMac) {
        new MacNativeHostService
      } else new NativeHostServices(getHostServices)
    }


    val services = logorrr.services.LogoRRRServices(
      SettingsIO.fromFile(FilePaths.settingsFilePath)
      , hostServices
      , new NativeOpenFileService(() => stage.getScene.getWindow)
      , isUnderTest = false)

    LogoRRRApp.start(stage, services)
  }

}