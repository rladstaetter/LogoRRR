package app.logorrr.conf

import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.model.LogFileSettings
import app.logorrr.util.CanLog
import com.typesafe.config.ConfigRenderOptions
import pureconfig.{ConfigSource, ConfigWriter}

import java.nio.file.Path
import scala.util.{Failure, Success, Try}

/**
 * pureconfig provides tools to de/serialize configuration which is in use here.
 * */
object SettingsIO extends CanLog {

  /** turn off ugly 'hardcoded value' messages */
  val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  def updateDividerPosition(path: Path, dividerPosition: Double): Unit = {
    LogoRRRGlobals.updateDividerPosition(path.toAbsolutePath.toString, dividerPosition)
  }

  def updateActiveLogFile(path: Path): Unit = {
    LogoRRRGlobals.setSomeActive(Option(path.toAbsolutePath.toString))
  }

  /** read settings from default place and filter all paths which don't exist anymore */
  def fromFile(): Settings = {
    val settingsFilePath = FilePaths.settingsFilePath
    Try(ConfigSource.file(settingsFilePath).loadOrThrow[Settings].filterWithValidPaths) match {
      case Failure(_) =>
        logWarn(s"Could not load $settingsFilePath, using default settings ...")
        Settings.Default
      case Success(value) => value
    }
  }

}
