package app.logorrr.views.main

import app.logorrr.io.FileId
import app.logorrr.services.file.FileIdService
import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.menubar.{FileMenu, HelpMenu}
import javafx.scene.control.MenuBar

class MainMenuBar(fileIdService: FileIdService
                  , openFile: FileId => Unit
                  , closeAllFiles: => Unit
                  , closeApplication: => Unit
                  , isUnderTest: Boolean)
  extends MenuBar
    with CanLog {

  val fileMenu = new FileMenu(isUnderTest, fileIdService, openFile, closeAllFiles, closeApplication)
  val helpMenu = new HelpMenu(openFile)

  setUseSystemMenuBar(OsUtil.isMac && !isUnderTest)
  setManaged(!OsUtil.isMac || isUnderTest) // set managed to false for mac to fix visual glitch (https://github.com/rladstaetter/LogoRRR/issues/179)

  private def init(): Unit = {
    getMenus.clear()
    getMenus.addAll(fileMenu, helpMenu)
  }

  init()
}
