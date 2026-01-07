package app.logorrr.io

import app.logorrr.conf.{Settings, SettingsMigrator}
import upickle.default.{read, write}

import java.nio.file.{Files, Path}
import scala.util.{Failure, Success, Try}

/**
 * supersimple file io for settings
 * */
object SettingsFileIO {

  def fromFile(path: Path): Try[Settings] = {
    val js = Files.readString(path)
    Try(read[Settings](js)) match {
      case Success(settings) => Try(settings)
      case Failure(exception) =>
        // retrying with migration if something went wrong
        val migrated = SettingsMigrator.migrate(js) // migrate from old style (pureconfig)
        Try(read[Settings](migrated))
    }
  }

  def toFile(settings: Settings, target: Path): Try[Unit] = {
    Try(Files.writeString(target, write(settings, indent = 2)))
  }

}
