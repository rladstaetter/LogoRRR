package app.logorrr.conf

import app.logorrr.model.LogFileSettings
import pureconfig.{ConfigReader, ConfigWriter}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

/**
 * Global settings for LogoRRR
 *
 * LogoRRR tries to remember as much as possible from last run, in order to give user a headstart from where they last
 * left. The idea is that the user doesn't need to fiddle around with settings every time.
 */
object Settings {

  implicit lazy val reader: ConfigReader[Settings] = deriveReader[Settings]
  implicit lazy val writer: ConfigWriter[Settings] = deriveWriter[Settings]

  val Default = Settings(
    StageSettings(0, 0, 500, 500)
    , Map()
    , None
  )

}

case class Settings(stageSettings: StageSettings
                    , logFileSettings: Map[String, LogFileSettings]
                    , someActive: Option[String]) {

  def remove(pathAsString: String): Settings = {
    copy(stageSettings
      , logFileSettings - pathAsString
      , None)
  }

  /** updates recent files with given log setting */
  def update(logFileSetting: LogFileSettings): Settings = {
    copy(stageSettings, logFileSettings + (logFileSetting.pathAsString -> logFileSetting))
  }

  def filterWithValidPaths(): Settings = copy(logFileSettings = logFileSettings.filter { case (_, d) => d.isPathValid })


}









