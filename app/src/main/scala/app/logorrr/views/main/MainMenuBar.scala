package app.logorrr.views.main

import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.scene.control.MenuBar
import javafx.stage.Window

import java.nio.file.Path

class MainMenuBar(getWindow: () => Window
                  , openLogFile: Path => Unit
                  , closeAllLogFiles: => Unit
                  , closeApplication: => Unit)
  extends MenuBar
    with CanLog {

  setUseSystemMenuBar(OsUtil.isMac)
  setManaged(!OsUtil.isMac) // set managed to false for mac to fix visual glitch (https://github.com/rladstaetter/LogoRRR/issues/179)

  private def init(): Unit = {
    getMenus.clear()
    getMenus.addAll(new FileMenu(getWindow, openLogFile, closeAllLogFiles, closeApplication), new HelpMenu(openLogFile))
  }

  init()
}
