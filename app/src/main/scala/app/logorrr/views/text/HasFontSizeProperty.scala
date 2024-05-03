package app.logorrr.views.text

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.HasFileId


trait HasFontSizeProperty extends HasFileId {

  def setFontSize(fontSize: Int): Unit = LogoRRRGlobals.getLogFileSettings(fileId).setFontSize(fontSize)

  def getFontSize: Int = LogoRRRGlobals.getLogFileSettings(fileId).getFontSize

}
