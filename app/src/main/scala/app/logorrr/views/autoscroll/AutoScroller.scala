package app.logorrr.views.autoscroll

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogIdAware

trait AutoScroller {
  autoScroller: LogIdAware =>

  def setAutoScroll(autoScroll: Boolean): Unit = LogoRRRGlobals.getLogFileSettings(pathAsString).setAutoScroll(autoScroll)

  def isAutoScroll(): Boolean = LogoRRRGlobals.getLogFileSettings(pathAsString).isAutoScroll

}
