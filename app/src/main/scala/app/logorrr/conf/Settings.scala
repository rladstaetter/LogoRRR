package app.logorrr.conf

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

  val Default = Settings(StageSettings(0, 0, 500, 500)
    , SquareImageSettings(10)
    , RecentFileSettings(Map(), None))

}

case class Settings(stageSettings: StageSettings
                    , squareImageSettings: SquareImageSettings
                    , recentFileSettings: RecentFileSettings) {

  def filterWithValidPaths: Settings = copy(recentFileSettings = recentFileSettings.filterValids())

}









