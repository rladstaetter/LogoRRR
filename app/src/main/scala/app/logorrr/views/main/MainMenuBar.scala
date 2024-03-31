package app.logorrr.views.main

import app.logorrr.io.FileId
import app.logorrr.services.fileservices.LogoRRRFileOpenService
import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.scene.control.MenuBar

class MainMenuBar(fileOpenService: LogoRRRFileOpenService
                  , openFile: FileId => Unit
                  , closeAllLogFiles: => Unit
                  , closeApplication: => Unit
                  , isUnderTest: Boolean)
  extends MenuBar
    with CanLog {

  val fileMenu = new FileMenu(fileOpenService, openFile, closeAllLogFiles, closeApplication)
  val helpMenu = new HelpMenu(openFile)

  setUseSystemMenuBar(OsUtil.isMac && !isUnderTest)
  setManaged(!OsUtil.isMac || isUnderTest) // set managed to false for mac to fix visual glitch (https://github.com/rladstaetter/LogoRRR/issues/179)

  private def init(): Unit = {
    getMenus.clear()
    getMenus.addAll(fileMenu, helpMenu)
  }

  init()
}
