package app.logorrr

import app.logorrr
import app.logorrr.conf.{AppInfo, LogoRRRGlobals, SettingsIO}
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.DefaultFileIdService
import app.logorrr.services.hostservices.{MacNativeHostService, NativeHostServices}
import app.logorrr.views.main.{LogoRRRMain, LogoRRRStage}
import javafx.application.Application
import javafx.stage.Stage
import net.ladstatt.app.{AppId, AppMeta}
import net.ladstatt.util.log.CanLog
import net.ladstatt.util.os.OsUtil

import java.nio.file.Paths

/**
 * Main starting point for LogoRRR Application
 */
// have fun and thanks for reading the code!
object LogoRRRApp extends CanLog:

  val Name = "LogoRRR"

  val appMeta: AppMeta = net.ladstatt.app.AppMeta(AppId(Name, "logorrr", "app.logorrr"), AppMeta.LogFormat)

  def main(args: Array[String]): Unit =
    net.ladstatt.app.AppMeta.initApp(appMeta)
    javafx.application.Application.launch(classOf[LogoRRRApp], args*)

  def start(stage: Stage
            , services: LogoRRRServices): LogoRRRMain =
    System.setProperty("user.language", "en")
    net.ladstatt.app.AppMeta.initApp(appMeta)
    LogoRRRNative.loadNativeLibraries()
    // make sure to set css before anything is initialized otherwise the rules won't apply
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")

    LogoRRRGlobals.set(services.settings, services.hostServices)
    val logoRRRMain = new LogoRRRMain(stage, services.fileIdService, services.isUnderTest)
    LogoRRRStage.init(stage, logoRRRMain)

    logInfo(s"          Started: ${AppInfo.fullAppNameWithVersion}")
    logInfo(s"Working directory: '${Paths.get("").toAbsolutePath.toString}'")
    logInfo(s"    Configuration: '$settingsFilePath'")
    logInfo(s"          Logfile: '$logFilePath'")
    logoRRRMain


class LogoRRRApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit =
    val hostServices =
      if OsUtil.isMac then
        new MacNativeHostService
      else new NativeHostServices(getHostServices)
    val settings = SettingsIO.fromFile(settingsFilePath)
    val services = logorrr.services.LogoRRRServices(
      settings
      , hostServices
      , new DefaultFileIdService(() => stage.getScene.getWindow)
      , isUnderTest = false)

    LogoRRRApp.start(stage, services)

}