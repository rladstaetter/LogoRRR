package app.logorrr.views.menubar

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.services.file.FileIdService
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

  /**
   * Menu Item to open a new file
   *
   * @param fileIdService underlying service to use
   * @param openFile function to change global state, add new file to UI ...
   */
  class OpenFileMenuItem(fileIdService: FileIdService
                         , openFile: FileId => Unit)
    extends MenuItem("Open") with CanLog {
    setId(UiNodes.FileMenuOpenFile.value)
    setOnAction(_ => {
      fileIdService.provideFileId match {
        case Some(fileId) => openFile(fileId)
        case None => logTrace("Cancel open file operation ...")
      }

    })
  }

  class CloseAllFilesMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setId(UiNodes.FileMenuCloseAll.value)
    setOnAction(_ => removeAllLogFiles)
  }

  class CloseApplicationMenuItem(closeApplication: => Unit) extends MenuItem("Quit") {
    setId(UiNodes.FileMenuCloseApplication.value)
    setOnAction(_ => closeApplication)
  }

}

class FileMenu(isUnderTest : Boolean
               , fileIdService: FileIdService
               , openFile: FileId => Unit
               , closeAllFiles: => Unit
               , closeApplication: => Unit) extends Menu("File")  {

  setId(UiNodes.FileMenu.value)

  lazy val openMenuItem = new FileMenu.OpenFileMenuItem(fileIdService, openFile)
  lazy val closeAllMenuItem = new FileMenu.CloseAllFilesMenuItem(closeAllFiles)
  lazy val closeApplicationMenuItem = new FileMenu.CloseApplicationMenuItem(closeApplication)

  def init(): Unit = {
    getItems.clear()
    getItems.add(openMenuItem)
    getItems.add(closeAllMenuItem)
    if (OsUtil.isLinux || OsUtil.isWin || isUnderTest) {
      getItems.add(closeApplicationMenuItem)
    }
  }

  init()
}