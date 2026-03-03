package app.logorrr.io

import app.logorrr.conf.Settings
import net.ladstatt.util.log.TinyLog
import upickle.default.{read, write}

import java.nio.file.{Files, Path}
import scala.util.{Failure, Success, Try}

/**
 * FileIO for settings
 * */
object SettingsFileIO extends TinyLog:

  def fromFile(source: Path): Settings =
    SettingsFileIO.from(source).map(_.filterWithValidPaths()) match {
      case Failure(e) =>
        val msg = s"Could not load settingsFile '${source.toAbsolutePath}', using default settings ..."
        logException(msg, e)
        logWarn(msg)
        Settings.Default
      case Success(value) =>
        logConfig(s"Loaded settings from '${source.toAbsolutePath}'.")
        value
    }

  def from(path: Path): Try[Settings] = Try(read[Settings](Files.readString(path)))

  def toFile(settings: Settings, target: Path): Try[Unit] =
    timeR(Try(Files.writeString(target, write(settings, indent = 2))), s"Writing ${target.toAbsolutePath}")

