package app.logorrr.conf

import app.logorrr.io.SettingsFileIO
import net.ladstatt.util.log.CanLog

import java.nio.file.Path
import scala.util.{Failure, Success}

/**
 * pureconfig provides tools to de/serialize configuration which is in use here.
 * */
object SettingsIO extends CanLog:

  def fromFile(source: Path): Settings = timeR({
    SettingsFileIO.fromFile(source).map(_.filterWithValidPaths()) match {
      case Failure(_) =>
        logWarn(s"Could not load $settingsFilePath, using default settings ...")
        Settings.Default
      case Success(value) =>
        logInfo(s"Loaded settings from '$settingsFilePath'.")
        value
    }
  }, s"Loading settings from ${settingsFilePath.toAbsolutePath}")


