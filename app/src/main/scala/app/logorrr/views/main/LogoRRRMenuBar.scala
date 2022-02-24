package app.logorrr.views.main

import app.logorrr.model.LogReportDefinition
import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.application.HostServices
import javafx.scene.control.MenuBar

import java.nio.file.Path

class LogoRRRMenuBar(hostServices: HostServices, openLogFile: Path => Unit, removeAllLogFiles: => Unit, updateLogDef: LogReportDefinition => Unit, closeApplication: => Unit) extends MenuBar
  with CanLog {

  if (OsUtil.isMac) {
    setUseSystemMenuBar(OsUtil.isMac)
  }

  def init(): Unit = {
    logTrace("Reinit Menu bar ...")
    getMenus.clear()
    getMenus.addAll(new FileMenu(openLogFile, removeAllLogFiles, updateLogDef, closeApplication), new HelpMenu(hostServices))
  }

  init()
}
