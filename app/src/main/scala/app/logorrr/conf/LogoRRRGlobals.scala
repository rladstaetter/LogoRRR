package app.logorrr.conf

import app.logorrr.conf.mut.MutSettings

/**
 * Place LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals {

  val settings = new MutSettings

  def set(settings: Settings): Unit = {
    this.settings.setRecentFileSettings(settings.recentFileSettings)
    this.settings.setSquareImageSettings(settings.squareImageSettings)
    this.settings.setStageSettings(settings.stageSettings)
  }

  def get(): Settings = settings.petrify()

}
