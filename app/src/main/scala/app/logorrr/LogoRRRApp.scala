package app.logorrr

import app.logorrr.conf.{LogoRRRGlobals, Settings, SettingsIO}
import app.logorrr.io.FilePaths
import app.logorrr.meta.AppMeta
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.main.{LogoRRRMain, LogoRRRStage}
import javafx.application.{Application, HostServices}
import javafx.stage.Stage

import java.nio.file.Paths

/**
 * Main starting point for LogoRRR Application
 */
// have fun and thanks for reading the code!
object LogoRRRApp {

  /** LogoRRRs own log formatting string */
  val LogFormat = """[%1$tF %1$tT.%1$tN] %3$-40s %4$-13s %5$s %6$s %n"""

  def main(args: Array[String]): Unit = {
    System.setProperty("user.language", "en")
    System.setProperty("java.util.logging.SimpleFormatter.format", LogFormat)
    // make sure to set css before anything is initialized otherwise the rules won't apply
    LogoRRRNative.loadNativeLibraries()

    javafx.application.Application.launch(classOf[LogoRRRApp], args: _*)
  }

  def start(stage: Stage
            , settings: Settings
            , hostServices: HostServices): Unit = {
    LogoRRRGlobals.set(settings, hostServices)
    val logoRRRMain = new LogoRRRMain(JfxUtils.closeStage(stage))
    LogoRRRStage.init(stage, logoRRRMain)
    logoRRRMain.initLogFilesFromConfig()
    LogoRRRStage.show(stage, logoRRRMain)
  }
}

class LogoRRRApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    logInfo(s"Started ${AppMeta.fullAppNameWithVersion} in '${Paths.get("").toAbsolutePath.toString}'")
    LogoRRRApp.start(stage, SettingsIO.fromFile(FilePaths.settingsFilePath), getHostServices)
  }

}