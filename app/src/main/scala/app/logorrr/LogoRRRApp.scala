package app.logorrr

import app.logorrr
import app.logorrr.conf.{AppInfo, LogoRRRGlobals}
import app.logorrr.io.{AppPaths, SettingsFileIO}
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.DefaultFileIdService
import app.logorrr.services.hostservices.{MacNativeHostService, NativeHostServices}
import app.logorrr.views.main.{LogoRRRMain, LogoRRRStage}
import javafx.application.Application
import javafx.stage.Stage
import net.ladstatt.util.io.TinyIo
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

import java.nio.file.{Files, Path, Paths}

/**
 * Main starting point for LogoRRR Application
 */
object LogoRRRApp extends TinyLog:

  val Name = "LogoRRR"

  val appInfo = AppInfo(Name, BuildProps.Instance)

  val paths = AppPaths("logorrr", "app.logorrr")
  // 1 log file, constrain it to 100 MB
  TinyLog.init(
    logFilePath = paths.logFile
    , limit = 1024 * 1024 * 100
    , count = 1)

  def main(args: Array[String]): Unit =
    javafx.application.Application.launch(classOf[LogoRRRApp], args *)

  def start(stage: Stage, services: LogoRRRServices): LogoRRRMain =
    System.setProperty("user.language", "en")
    LogoRRRNative.loadNativeLibraries()
    // make sure to set css before anything is initialized otherwise the rules won't apply
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")

    LogoRRRGlobals.set(services.settings, services.hostServices)
    
    val logoRRRMain = new LogoRRRMain(stage
      , services.fileIdService
      , services.isUnderTest
      , services.settings.someActive)
    LogoRRRStage.init(stage, logoRRRMain)

    logInfo(s"            Started: ${appInfo.asString}")
    logConfig(s"Working directory: '${Paths.get("").toAbsolutePath.toString}'")
    logConfig(s"     Program data: '${paths.appDataDirectory.toAbsolutePath.toString}'")
    logConfig(s"    Configuration: '${paths.settingsFile}'")
    logConfig(s"          Logfile: '${paths.logFile}'")
    logoRRRMain


class LogoRRRApp extends javafx.application.Application with TinyIo with TinyLog:

  private val paths = LogoRRRApp.paths

  def start(stage: Stage): Unit =
    val hostServices =
      if OsUtil.isMac then
        new MacNativeHostService
      else new NativeHostServices(getHostServices)

    val legacyPath: Path = paths.appDataDirectory.resolve(paths.groupId + ".conf")

    val settings =
      if Files.exists(legacyPath) then
        logInfo(s"Reading old settings file $legacyPath.")
        val s = SettingsFileIO.fromFile(legacyPath)
        val backupPath = paths.appDataDirectory.resolve(paths.groupId + ".conf_backup")
        copy(legacyPath, backupPath)
        Files.delete(legacyPath)
        logInfo(s"Deleted old settings file '$legacyPath', will now use '${paths.settingsFile}'.")
        s
      else
        SettingsFileIO.fromFile(paths.settingsFile)

    val services = logorrr.services.LogoRRRServices(
      settings
      , hostServices
      , new DefaultFileIdService(() => stage.getScene.getWindow)
      , isUnderTest = false)

    LogoRRRApp.start(stage, services)

