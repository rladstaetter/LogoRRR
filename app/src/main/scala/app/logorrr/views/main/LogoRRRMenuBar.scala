package app.logorrr.views.main

import app.logorrr.util.OsUtil
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.application.HostServices
import javafx.scene.control.MenuBar

import java.nio.file.Path

class LogoRRRMenuBar(openLogFile: Path => Unit
                     , removeAllLogFiles: => Unit
                     , closeApplication: => Unit
                     , hostServices: HostServices) extends MenuBar {

  if (OsUtil.isMac) {
    setUseSystemMenuBar(OsUtil.isMac)
  }

  def init(): Unit = {
    getMenus.clear()
    getMenus.addAll(new FileMenu(openLogFile, removeAllLogFiles, closeApplication), new HelpMenu(hostServices))
  }

  init()
}
