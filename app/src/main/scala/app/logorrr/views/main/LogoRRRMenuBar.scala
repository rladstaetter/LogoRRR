package app.logorrr.views.main

import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.scene.control.MenuBar

import java.nio.file.Path

class LogoRRRMenuBar(openLogFile: Path => Unit
                     , closeApplication: => Unit)
  extends MenuBar
    with CanLog {

  if (OsUtil.isMac) {
    setUseSystemMenuBar(OsUtil.isMac)
  }

  private def init(): Unit = {
    getMenus.clear()
    getMenus.addAll(new FileMenu(openLogFile, closeApplication), new HelpMenu())
  }

  init()
}
