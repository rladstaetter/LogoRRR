package app.logorrr.views.menubar

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.services.file.FileService
import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.UiNodes
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{FileChooser, Window}

/**
 * Helper class to group file open operation and setting LogoRRRGlobals
 *
 * @param title title of file dialog (no effect on mac?)
 */
class LogoRRRFileChooser(title: String) {

  def performShowAndWait(window: Window): Option[FileId] = {
    val fc = new FileChooser
    fc.setTitle(title)
    LogoRRRGlobals.getSomeLastUsedDirectory.foreach(d => fc.setInitialDirectory(d.toFile))
    val someFileId: Option[FileId] = Option(fc.showOpenDialog(window)).map(f => FileId(f.toPath))
    LogoRRRGlobals.setSomeLastUsedDirectory(someFileId.map(fileId => fileId.asPath.getParent))
    LogoRRRGlobals.persist()
    someFileId
  }

}


object FileMenu {

  class OpenMenuItem(fileOpenService: FileService
                     , openFile: FileId => Unit)
    extends MenuItem("Open") with CanLog {
    setId(UiNodes.FileMenuOpenFile.value)
    setOnAction(_ => {
      fileOpenService.openFile match {
        case Some(fileId) => openFile(fileId)
        case None => logTrace("Cancelled open file operation ...")
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
                ,fileOpenService: FileService
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