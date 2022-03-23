package app.logorrr.views.menubar

import app.logorrr.model
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, LogoRRRFileChooser, OsUtil}
import javafx.scene.control.{Menu, MenuItem}

import java.nio.file.{Files, Path}

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
               , removeAllLogFiles: => Unit
               , updateLogDef: LogFileSettings => Unit
               , closeApplication: => Unit) extends Menu("File") with CanLog {
  /*
    val settings = {
      logTrace("Reinit File Menu ...")
      SettingsIO.someSettings.getOrElse(Settings.Default)
    }

    val recentFiles: Seq[String] = settings.recentFiles.logReportDefinition.map(_.pathAsString)

    val recentFilesMenu = RecentFilesMenu(recentFiles.map(f => Paths.get(f)), updateLogDef)
  */
  def init(): Unit = {
    getItems.clear()
    getItems.add(new FileMenu.OpenMenuItem(openLogFile))
    /*
        if (recentFiles.nonEmpty) {
          getItems.add(recentFilesMenu)
          val closeAllMenuItems = new FileMenu.CloseAllMenuItem(removeAllLogFiles)
          getItems.add(closeAllMenuItems)
        }
    */
    if (OsUtil.isWin) {
      getItems.add(new FileMenu.QuitMenuItem(closeApplication))
    }
  }

  init()
}