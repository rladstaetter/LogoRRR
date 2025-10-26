package app.logorrr.views.main

import app.logorrr.io.FileId
import app.logorrr.services.file.FileIdService
import app.logorrr.views.menubar.{FileMenu, HelpMenu, OsxAppMenu}
import de.jangassen.MenuToolkit
import de.jangassen.model.AppearanceMode
import javafx.scene.control.MenuBar
import javafx.stage.Stage

class MainMenuBar(stage: Stage
                  , fileIdService: FileIdService
                  , openFile: FileId => Unit
                  , closeAllFiles: => Unit
                  , isUnderTest: Boolean) extends MenuBar {

  val tk: MenuToolkit = MenuToolkit.toolkit
  tk.setAppearanceMode(AppearanceMode.AUTO)

  private val appMenu = OsxAppMenu.mkMenu(stage, tk, isUnderTest)
  private val fileMenu = new FileMenu(fileIdService, openFile, closeAllFiles)
  private val helpMenu = new HelpMenu(stage, openFile)

  //   setUseSystemMenuBar(OsUtil.isMac && !isUnderTest)
  //   setManaged(!OsUtil.isMac || isUnderTest) // set managed to false for mac to fix visual glitch (https://github.com/rladstaetter/LogoRRR/issues/179)
  getMenus.addAll(appMenu, fileMenu, helpMenu)
  if (!isUnderTest) {
    tk.setGlobalMenuBar(this)
  }

}
