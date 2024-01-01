package app.logorrr.conf

import app.logorrr.io.{FileId, IoManager}
import app.logorrr.model.LogFileSettings
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

import java.nio.file.Path

/**
 * Global settings for LogoRRR
 *
 * LogoRRR tries to remember as much as possible from last run, in order to give user a headstart from where they last
 * left. The idea is that the user doesn't need to fiddle around with settings every time.
 */
object Settings {

  implicit lazy val reader: ConfigReader[Settings] = deriveReader[Settings]
  implicit lazy val writer: ConfigWriter[Settings] = deriveWriter[Settings]

  val Default: Settings = Settings(
    StageSettings(100, 100, 800, 600)
    , Map()
    , None
    , None
  )

}

case class Settings(stageSettings: StageSettings
                    , fileSettings: Map[String, LogFileSettings] // has to stay Map[String,LogFileSettings] because of Reader/Writer derivation
                    , someActive: Option[FileId]
                    , someLastUsedDirectory: Option[Path]) {

  /** updates recent files with given log setting */
  def update(logFileSetting: LogFileSettings): Settings = {
    copy(stageSettings, fileSettings + (logFileSetting.fileId.value -> logFileSetting))
  }

  def filterWithValidPaths(): Settings = copy(fileSettings = fileSettings.filter { case (_, d) =>
    // if entry is part of a zip file, test the path of the zip file
    if (d.fileId.isZipEntry) {
      IoManager.isPathValid(d.fileId.extractZipFileId.asPath)
    } else {
      IoManager.isPathValid(d.path)
    }
  })


}









