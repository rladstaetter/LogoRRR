package app.logorrr.views.main

import app.logorrr.conf.FileId
import app.logorrr.services.file.FileIdService
import app.logorrr.views.menubar.{FileMenu, HelpMenu, AppMenuBuilder}
import de.jangassen.MenuToolkit
import de.jangassen.model.AppearanceMode
import javafx.scene.control.{Menu, MenuBar}
import javafx.stage.Stage
import net.ladstatt.util.os.OsUtil

class MainMenuBar(stage: Stage
                  , fileIdService: FileIdService
                  , openFile: FileId => Unit
                  , closeAllFiles: => Unit
                  , isUnderTest: Boolean) extends MenuBar {

  if (OsUtil.isMac) {
    val tk: MenuToolkit = MenuToolkit.toolkit
    tk.setAppearanceMode(AppearanceMode.AUTO)

    val appMenu = AppMenuBuilder.mkOsxMenu(stage, tk, isUnderTest)
    val fileMenu = new FileMenu(fileIdService, openFile, closeAllFiles)
    val helpMenu = new HelpMenu(stage, openFile)
    getMenus.addAll(appMenu, fileMenu, helpMenu)
    if (!isUnderTest) {
      tk.setGlobalMenuBar(this)
    }
  } else {
    val appMenu: Menu = AppMenuBuilder.mkMenu(stage, isUnderTest)
    val fileMenu = new FileMenu(fileIdService, openFile, closeAllFiles)
    val helpMenu = new HelpMenu(stage, openFile)
    getMenus.addAll(appMenu, fileMenu, helpMenu)
  }

}
