package app.logorrr.views.menubar

import app.logorrr.conf.Settings
import app.logorrr.util.{CanLog, LogoRRRFileChooser, OsUtil}
import app.logorrr.views.menubar.FileMenu.OpenRecentMenu
import javafx.scene.control.{Menu, MenuItem}

import java.nio.file.{Path, Paths}

object FileMenu {

  class OpenMenuItem(openLogFile: Path => Unit)
    extends MenuItem("Open") with CanLog {

    setOnAction(_ => {
      new LogoRRRFileChooser("Open log file").showAndWait() match {
        case Some(logFile) =>
          Settings.updateRecentFileSettings(rf => rf.copy(files = logFile.toAbsolutePath.toString +: rf.files))
          openLogFile(logFile)
        case None => logInfo("Cancelled open file ...")
      }

    })
  }

  class QuitMenuItem(closeApplication: => Unit) extends MenuItem("Quit") {
    setOnAction(e => closeApplication)
  }

  object OpenRecentMenu {

    class RecentFileMenuItem(path: Path
                             , openLogFile: Path => Unit) extends MenuItem(path.getFileName.toString) {
      setOnAction(_ => {
        openLogFile(path)
      })
    }

    class OpenRecentMenu extends Menu("Open Recent")

    def menu(openLogFile: Path => Unit, recentFiles: Seq[Path]): Menu = {
      val m = new OpenRecentMenu
      m.getItems.addAll(recentFiles.map(p => new OpenRecentMenu.RecentFileMenuItem(p, openLogFile)): _*)
      m
    }
  }

  class CloseAllMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setOnAction(_ => removeAllLogFiles)
  }

}

class FileMenu(openLogFile: Path => Unit
               , removeAllLogFiles: => Unit
               , closeApplication: => Unit) extends Menu("File") {

  val settings = Settings.read()

  private val recentFiles: Seq[String] = settings.recentFiles.files

  val recentFilesMenu = OpenRecentMenu.menu(openLogFile, recentFiles.map(f => Paths.get(f)))

  def init(): Unit = {
    getItems.clear()
    getItems.add(new FileMenu.OpenMenuItem(openLogFile))

    if (recentFiles.nonEmpty) {
      getItems.add(recentFilesMenu)
      val closeAllMenuItems = new FileMenu.CloseAllMenuItem(removeAllLogFiles)
      getItems.add(closeAllMenuItems)
    }

    if (OsUtil.isWin) {
      getItems.add(new FileMenu.QuitMenuItem(closeApplication))
    }
  }

  init()
}