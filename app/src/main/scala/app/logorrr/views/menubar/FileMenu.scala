package app.logorrr.views.menubar

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.services.fileservices.LogoRRRFileOpenService
import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.UiNodes
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{FileChooser, Window}

import java.nio.file.Path

class LogoRRRFileChooser(title: String) {

  def showAndWait(window: Window): Option[Path] = {
    val fc = new FileChooser
    fc.setTitle(title)
    LogoRRRGlobals.getSomeLastUsedDirectory.foreach(d => fc.setInitialDirectory(d.toFile))
    val somePath = Option(fc.showOpenDialog(window)).map(_.toPath)
    LogoRRRGlobals.setSomeLastUsedDirectory(somePath.map(_.getParent))
    LogoRRRGlobals.persist()
    somePath
  }

}


object FileMenu {

  class OpenMenuItem(fileOpenService: LogoRRRFileOpenService
                     , openFile: FileId => Unit)
    extends MenuItem("Open") with CanLog {
    setId(UiNodes.FileMenuOpenFile.value)
    setOnAction(_ => {
      fileOpenService.openFile match {
        case Some(logFile) => openFile(FileId(logFile))
        case None => logTrace("Cancelled open file ...")
      }

    })
  }

  class CloseAllMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setId(UiNodes.FileMenuCloseAll.value)
    setOnAction(_ => removeAllLogFiles)
  }

  class QuitMenuItem(closeApplication: => Unit) extends MenuItem("Quit") {
    setId(UiNodes.FileMenuQuitApplication.value)
    setOnAction(_ => closeApplication)
  }

}

class FileMenu(isUnderTest : Boolean
                ,fileOpenService: LogoRRRFileOpenService
               , openFile: FileId => Unit
               , closeAllLogFiles: => Unit
               , closeApplication: => Unit) extends Menu("File") with CanLog {

  setId(UiNodes.FileMenu.value)

  lazy val openMenuItem = new FileMenu.OpenMenuItem(fileOpenService, openFile)
  lazy val closeAllMenuItem = new FileMenu.CloseAllMenuItem(closeAllLogFiles)
  lazy val quitMenuItem = new FileMenu.QuitMenuItem(closeApplication)

  def init(): Unit = {
    getItems.clear()
    getItems.add(openMenuItem)
    getItems.add(closeAllMenuItem)
    if (OsUtil.isLinux || OsUtil.isWin || isUnderTest) {
      getItems.add(quitMenuItem)
    }
  }

  init()
}