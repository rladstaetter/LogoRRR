package app.logorrr.views.menubar

import app.logorrr.io.FileId
import app.logorrr.services.file.FileIdService
import javafx.scene.control.{Menu, MenuItem}
import net.ladstatt.util.log.CanLog


object FileMenu {

  /**
   * Menu Item to open a new file
   *
   * @param fileIdService underlying service to use
   * @param openFile      function to change global state, add new file to UI ...
   */
  class OpenFileMenuItem(fileIdService: FileIdService
                         , openFile: FileId => Unit)
    extends MenuItem("Open...") with CanLog {
    setId(app.logorrr.views.a11y.uinodes.FileMenu.OpenFile.value)
    setOnAction(_ => {
      fileIdService.provideFileId match {
        case Some(fileId) => openFile(fileId)
        case None => logTrace("Cancel open file operation ...")
      }

    })
  }

  class CloseAllFilesMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setId(app.logorrr.views.a11y.uinodes.FileMenu.CloseAll.value)
    setOnAction(_ => removeAllLogFiles)
  }


}




class FileMenu(fileIdService: FileIdService
               , openFile: FileId => Unit
               , closeAllFiles: => Unit) extends Menu("File") {

  setId(app.logorrr.views.a11y.uinodes.FileMenu.Self.value)

  lazy val openMenuItem = new FileMenu.OpenFileMenuItem(fileIdService, openFile)
  lazy val closeAllMenuItem = new FileMenu.CloseAllFilesMenuItem(closeAllFiles)

  def init(): Unit = {
    getItems.clear()
    getItems.add(openMenuItem)
    getItems.add(closeAllMenuItem)
  }

  init()
}