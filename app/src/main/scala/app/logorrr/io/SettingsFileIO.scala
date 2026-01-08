package app.logorrr.io

import app.logorrr.conf.{Settings, SettingsMigrator}
import net.ladstatt.util.log.CanLog
import upickle.default.{read, write}

import java.nio.file.{Files, Path}
import scala.util.{Failure, Success, Try}

/**
 * supersimple file io for settings
 * */
object SettingsFileIO extends CanLog:

  def fromFile(path: Path): Try[Settings] = Try:
    val js = Files.readString(path)
    Try(read[Settings](js)) match
      case Success(settings) => settings
      case Failure(exception) =>
        // retrying with migration if something went wrong
        val migrated = SettingsMigrator.migrate(js) // migrate from old style (pureconfig)
        read[Settings](migrated)

  def toFile(settings: Settings, target: Path): Try[Unit] =
    Try(timeR(Files.writeString(target, write(settings, indent = 2)), s"Wrote $target"))

