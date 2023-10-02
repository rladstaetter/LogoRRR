package app.logorrr.views.text

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogIdAware


trait HasFontSizeProperty extends LogIdAware {

  def setFontSize(fontSize: Int): Unit = LogoRRRGlobals.getLogFileSettings(pathAsString).setFontSize(fontSize)

  def getFontSize: Int = LogoRRRGlobals.getLogFileSettings(pathAsString).getFontSize

}
