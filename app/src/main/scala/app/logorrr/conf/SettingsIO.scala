package app.logorrr.conf

import app.logorrr.conf.Settings.Default
import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.model.LogFileDefinition
import app.logorrr.util.CanLog
import com.typesafe.config.ConfigRenderOptions
import pureconfig.{ConfigSource, ConfigWriter}

import java.nio.file.Path
import scala.util.{Failure, Success, Try}

object SettingsIO extends CanLog {

  /** turn of ugly 'hardcoded value' messages */
  val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  /** persists settings */
  def write(settings: Settings): Unit = {
    Fs.write(FilePaths.settingsFilePath, ConfigWriter[Settings].to(settings).render(renderOptions))
  }

  /** update recent files */
  def updateRecentFileSettings(updateRecentFilesFn: RecentFileSettings => RecentFileSettings): Unit = {
    val settings = read()
    SettingsIO.write(settings.copy(recentFiles = updateRecentFilesFn(settings.recentFiles)))
  }

  def updateDividerPosition(path: Path, dividerPosition: Double): Unit = {
    val settings = read()
    val recentFiles = settings.recentFiles
    val lrd = recentFiles.logFileDefinitions(path.toAbsolutePath.toString).copy(dividerPosition = dividerPosition)
    val nrf = recentFiles.copy(logFileDefinitions = settings.recentFiles.logFileDefinitions + (path.toAbsolutePath.toString -> lrd))
    SettingsIO.write(settings.copy(recentFiles = nrf))
  }

  def updateActiveLogFile(path: Path): Unit = {
    val settings = read()
    val recentFiles = settings.recentFiles
    val lrd = recentFiles.logFileDefinitions(path.toAbsolutePath.toString).copy(active = true)
    val nrf = recentFiles.copy(logFileDefinitions = settings.recentFiles.logFileDefinitions + (path.toAbsolutePath.toString -> lrd))
    SettingsIO.write(settings.copy(recentFiles = nrf))
  }

  /** read settings from default place and filter all paths which don't exist anymore */
  def read(): Settings = {
    Try(ConfigSource.file(FilePaths.settingsFilePath).loadOrThrow[Settings].filterWithValidPaths) match {
      case Failure(ex) =>
        logException(s"Could not load ${FilePaths.settingsFilePath}, using default settings ...", ex)
        Settings.Default
      case Success(value) => value
    }
  }

  /** has to be a def to reread every time this is accessed */
  def someSettings: Option[Settings] =
    Try(read()) match {
      case Success(settings) =>
        logInfo(s"Loaded settings from ${FilePaths.settingsFilePath.toAbsolutePath} ...")
        Option(settings)
      case Failure(_) =>
        Try(ConfigSource.default.loadOrThrow[Settings]) match {
          case Success(defaultSettings) =>
            logError(s"Could not load settings, reinitializing ${FilePaths.settingsFilePath.toAbsolutePath} with default settings ...")
            SettingsIO.write(defaultSettings)
            Option(defaultSettings)
          // should not happen, programming / deployment error
          case Failure(e) =>
            logException("Could not load config default source ...", e)
            //logError("Using following fallback configuration:")
            //logError("")
            //logError(SettingsWriter.to(Default))
            //logError("")
            Option(Default)
        }
    }

}
