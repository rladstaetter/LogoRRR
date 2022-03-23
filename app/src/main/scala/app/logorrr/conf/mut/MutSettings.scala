package app.logorrr.conf.mut

import app.logorrr.conf.{RecentFileSettings, Settings, SquareImageSettings, StageSettings}

object MutSettings {

  def apply(settings: Settings): MutSettings = {
    val s = new MutSettings
    s.setStageSettings(settings.stageSettings)
    s.setSquareImageSettings(settings.squareImageSettings)
    s.setRecentFileSettings(settings.recentFileSettings)
    s
  }

}


class MutSettings {

  val stageSettings = new MutStageSettings
  val squareImageSettings = new MutSquareImageSettings
  val recentFileSettings = new MutRecentFileSettings

  def setRecentFileSettings(recentFileSettings: RecentFileSettings): Unit = {
    this.recentFileSettings.setLogFileDefinitions(recentFileSettings.logFileDefinitions)
    this.recentFileSettings.setSomeActiveLogReport(recentFileSettings.someActiveLogReport)
  }

  def setSquareImageSettings(squareImageSettings: SquareImageSettings): Unit = {
    this.squareImageSettings.setWidth(squareImageSettings.width)
  }

  def setStageSettings(stageSettings: StageSettings): Unit = {
    this.stageSettings.setX(stageSettings.x)
    this.stageSettings.setY(stageSettings.y)
    this.stageSettings.setHeight(stageSettings.height)
    this.stageSettings.setWidth(stageSettings.width)
  }

  def petrify(): Settings = {
    Settings(this.stageSettings.petrify()
      , this.squareImageSettings.petrify()
      , this.recentFileSettings.petrify())
  }
}