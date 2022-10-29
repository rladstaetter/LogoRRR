package app.logorrr.views.text

import app.logorrr.conf.LogoRRRGlobals

trait HasFontSizeProperty {

  def pathAsString: String

  def setFontSize(fontSize: Int): Unit = LogoRRRGlobals.getLogFileSettings(pathAsString).setFontSize(fontSize)

  def getFontSize(): Int = LogoRRRGlobals.getLogFileSettings(pathAsString).getFontSize()

}
