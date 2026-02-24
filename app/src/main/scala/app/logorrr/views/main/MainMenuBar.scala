package app.logorrr.views.main

import app.logorrr.conf.FileId
import app.logorrr.services.file.FileIdService
import app.logorrr.views.menubar.{AppMenuBuilder, FileMenu, HelpMenu}
import app.logorrr.views.search.st.FavoritesComboBox
import de.jangassen.MenuToolkit
import de.jangassen.model.AppearanceMode
import javafx.scene.control.{Menu, MenuBar}
import javafx.stage.Stage
import net.ladstatt.util.os.OsUtil

class MainMenuBar(stage: Stage
                  , main: LogoRRRMain
                  , fileIdService: FileIdService
                  , openFile: FileId => Unit
                  , closeAllFiles: => Unit
                  , isUnderTest: Boolean) extends MenuBar:

  if OsUtil.isMac then
    val tk: MenuToolkit = MenuToolkit.toolkit
    tk.setAppearanceMode(AppearanceMode.AUTO)
    addMenu(AppMenuBuilder.mkOsxMenu(stage, tk, main, isUnderTest))
    if !isUnderTest then tk.setGlobalMenuBar(this)
  else
    addMenu(AppMenuBuilder.mkMenu(stage, main, isUnderTest))

  private def addMenu(appMenu: Menu): Unit =
    val fileMenu = new FileMenu(fileIdService, openFile, closeAllFiles)
    val helpMenu = new HelpMenu(stage, openFile)
    getMenus.addAll(appMenu, fileMenu, helpMenu)
