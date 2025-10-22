package app.logorrr.views.menubar

import app.logorrr.LogoRRRApp
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.services.file.FileIdService
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.UiNodes
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{FileChooser, Stage, Window}
import net.ladstatt.util.log.CanLog
import net.ladstatt.util.os.OsUtil

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
   * @param openFile      function to change global state, add new file to UI ...
   */
  class OpenFileMenuItem(fileIdService: FileIdService
                         , openFile: FileId => Unit)
    extends MenuItem("Open") with CanLog {
    setId(UiNodes.FileMenu.OpenFile.value)
    setOnAction(_ => {
      fileIdService.provideFileId match {
        case Some(fileId) => openFile(fileId)
        case None => logTrace("Cancel open file operation ...")
      }

    })
  }

  class CloseAllFilesMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setId(UiNodes.FileMenu.CloseAll.value)
    setOnAction(_ => removeAllLogFiles)
  }


}

object CloseApplicationMenuItem {

  def apply(menuItem: MenuItem, stage:Stage) : MenuItem = {
    menuItem.setId(UiNodes.FileMenu.CloseApplication.value)
    Option(menuItem.getOnAction) match {
      case Some(value) =>
        menuItem.setOnAction(e => {
          JfxUtils.closeStage(stage)
          value.handle(e) // 'natively' quit application on macosx
        })
      case None =>
        menuItem.setOnAction(_ => JfxUtils.closeStage(stage))
    }
    menuItem
  }

}



class FileMenu(stage : Stage
,                isUnderTest: Boolean
               , fileIdService: FileIdService
               , openFile: FileId => Unit
               , closeAllFiles: => Unit) extends Menu("File") {

  setId(UiNodes.FileMenu.Self.value)

  lazy val openMenuItem = new FileMenu.OpenFileMenuItem(fileIdService, openFile)
  lazy val closeAllMenuItem = new FileMenu.CloseAllFilesMenuItem(closeAllFiles)
  lazy val closeApplicationMenuItem = CloseApplicationMenuItem(new MenuItem(s"Quit ${LogoRRRApp.Name}"), stage)

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