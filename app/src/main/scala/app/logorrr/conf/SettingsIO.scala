package app.logorrr.conf

import app.logorrr.io.FilePaths
import app.logorrr.util.CanLog
import com.typesafe.config.ConfigRenderOptions
import pureconfig.ConfigSource

import java.nio.file.Path
import scala.util.{Failure, Success, Try}

/**
 * pureconfig provides tools to de/serialize configuration which is in use here.
 * */
object SettingsIO extends CanLog {

  /** turn off ugly 'hardcoded value' messages */
  val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  /** read settings from default place and filter all paths which don't exist anymore */
  def fromFile(): Settings = {
    val settingsFilePath = FilePaths.settingsFilePath
    Try(ConfigSource.file(settingsFilePath).loadOrThrow[Settings].filterWithValidPaths()) match {
      case Failure(_) =>
        logWarn(s"Could not load $settingsFilePath, using default settings ...")
        Settings.Default
      case Success(value) =>
        logTrace(s"vi \"${settingsFilePath}\"")
        value
    }
  }

}
