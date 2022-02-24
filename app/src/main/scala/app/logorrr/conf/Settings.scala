package app.logorrr.conf

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object Settings {

  implicit lazy val reader = deriveReader[Settings]
  implicit lazy val writer = deriveWriter[Settings]

  val Default = Settings(StageSettings(0, 0, 500, 500)
    , SquareImageSettings(10)
    , RecentFileSettings(Map(), None))

}

case class Settings(stageSettings: StageSettings
                    , squareImageSettings: SquareImageSettings
                    , recentFiles: RecentFileSettings) {

  def filterWithValidPaths: Settings = copy(recentFiles = recentFiles.filterValids())

}


