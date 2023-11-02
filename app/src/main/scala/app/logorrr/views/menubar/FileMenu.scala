package app.logorrr.views.menubar

 import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.util.{CanLog, OsUtil}
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

  class OpenMenuItem(getWindow: () => Window
                     , openLogFile: Path => Unit)
    extends MenuItem("Open") with CanLog {

    setOnAction(_ => {
      new LogoRRRFileChooser("Open log file").showAndWait(getWindow()) match {
        case Some(logFile) => openLogFile(logFile)
        case None => logInfo("Cancelled open file ...")
      }

    })
  }

  class CloseAllMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setOnAction(_ => removeAllLogFiles)
  }

  class QuitMenuItem(closeApplication: => Unit) extends MenuItem("Quit") {
    setOnAction(_ => closeApplication)
  }

}

class FileMenu(getWindow: () => Window
               , openLogFile: Path => Unit
               , closeAllLogFiles: => Unit
               , closeApplication: => Unit) extends Menu("File") with CanLog {

  def init(): Unit = {
    getItems.clear()
    // see issue https://github.com/rladstaetter/LogoRRR/issues/146
    // for the moment, this menu item is not shown on mac as a workaround
    if (!OsUtil.isMac) {
      getItems.add(new FileMenu.OpenMenuItem(getWindow, openLogFile))
    }
    getItems.add(new FileMenu.CloseAllMenuItem(closeAllLogFiles))
    if (OsUtil.isWin) {
      getItems.add(new FileMenu.QuitMenuItem(closeApplication))
    }
  }

  init()
}