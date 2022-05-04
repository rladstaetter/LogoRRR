package app.logorrr.views.menubar

import app.logorrr.util.{CanLog, LogoRRRFileChooser, OsUtil}
import javafx.scene.control.{Menu, MenuItem}

import java.nio.file.Path

object FileMenu {

  class OpenMenuItem(openLogFile: Path => Unit)
    extends MenuItem("Open") with CanLog {

    setOnAction(_ => {
      new LogoRRRFileChooser("Open log file").showAndWait() match {
        case Some(logFile) =>
          openLogFile(logFile)
        case None => logInfo("Cancelled open file ...")
      }

    })
  }

  class QuitMenuItem(closeApplication: => Unit) extends MenuItem("Quit") {
    setOnAction(e => closeApplication)
  }

  object RecentFilesMenu {


    object RecentFileMenu {


    }


    class RecentFileMenu(path: Path) extends Menu(path.getFileName.toString) {

    }


  }

  case class RecentFilesMenu(paths: Seq[Path]) extends Menu("Log Files") {
    getItems.addAll(paths.map(p => new RecentFilesMenu.RecentFileMenu(p)): _*)
  }


  class CloseAllMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setOnAction(_ => removeAllLogFiles)
  }

}

class FileMenu(openLogFile: Path => Unit
               , closeApplication: => Unit) extends Menu("File") with CanLog {

  def init(): Unit = {
    getItems.clear()
    getItems.add(new FileMenu.OpenMenuItem(openLogFile))
    if (OsUtil.isWin) {
      getItems.add(new FileMenu.QuitMenuItem(closeApplication))
    }
  }

  init()
}