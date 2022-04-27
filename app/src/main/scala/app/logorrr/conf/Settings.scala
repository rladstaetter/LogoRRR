package app.logorrr.conf

import app.logorrr.model.LogFileSettings
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

/**
 * Global settings for LogoRRR
 *
 * LogoRRR tries to remember as much as possible from last run, in order to give user a headstart from where they last
 * left. The idea is that the user doesn't need to fiddle around with settings every time.
 */
object Settings {

  implicit lazy val reader = deriveReader[Settings]
  implicit lazy val writer = deriveWriter[Settings]

  val Default = Settings(
    StageSettings(0, 0, 500, 500)
    , Map()
    , Seq()
    , None
  )

}

case class Settings(stageSettings: StageSettings
                    , logFileSettings: Map[String, LogFileSettings]
                    , logFileOrdering: Seq[String]
                    , someActive: Option[String]) {

  def asOrderedSeq: Seq[LogFileSettings] = logFileOrdering.map(logFileSettings.apply)

  def remove(pathAsString: String): Settings = {
    val logOrdering = logFileOrdering.filterNot(_ == pathAsString)
    copy(stageSettings
      , logFileSettings - pathAsString
      , logOrdering
      , None)
  }

  /** updates recent files with given log setting */
  def update(logFileSetting: LogFileSettings): Settings = {
    val newPath = logFileSetting.pathAsString
    val nlo =
      if (!logFileOrdering.toSet.contains(newPath)) {
        logFileOrdering :+ newPath
      } else {
        logFileOrdering
      }
    copy(stageSettings, logFileSettings + (newPath -> logFileSetting), logFileOrdering = nlo)
  }

  def filterWithValidPaths(): Settings = copy(logFileSettings = logFileSettings.filter { case (_, d) => d.isPathValid })


}









