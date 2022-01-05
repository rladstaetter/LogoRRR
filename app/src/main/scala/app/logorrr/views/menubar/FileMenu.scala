package app.logorrr.views.menubar

import app.logorrr.conf.Settings
import app.logorrr.util.OsUtil
import app.logorrr.views.menubar.FileMenu.{CloseAllMenuItem, OpenMenuItem, OpenRecentMenu}
import javafx.scene.control.{Menu, MenuItem, SeparatorMenuItem}

import java.nio.file.{Files, Path, Paths}

object FileMenu {

  object OpenMenuItem extends MenuItem("Open")

  object QuitMenuItem extends MenuItem("Quit") {

  }

  object OpenRecentMenu {

    class RecentFileMenuItem(path: Path) extends MenuItem(path.getFileName.toString)

    object RemoveAllRecentFilesMenuItem extends MenuItem("Remove all")

    class OpenRecentMenu extends Menu("Open Recent")

    def menu(recentFiles: Seq[Path]): Menu = {
      val m = new OpenRecentMenu
      m.getItems.addAll(recentFiles.map(p => new OpenRecentMenu.RecentFileMenuItem(p)): _*)
      m.getItems.add(new SeparatorMenuItem())
      m.getItems.add(OpenRecentMenu.RemoveAllRecentFilesMenuItem)
      m
    }
  }

  object CloseAllMenuItem extends MenuItem("Close All")

}

class FileMenu(settings: Settings) extends Menu("File") {

  def init(): Unit = {
    getItems.add(OpenMenuItem)
    if (settings.recentFiles.files.nonEmpty) {
      getItems.add(OpenRecentMenu.menu(settings.recentFiles.files.map(f => Paths.get(f)).filter(p => Files.exists(p))))
    }
    getItems.add(CloseAllMenuItem)
    if (OsUtil.isWin) {
      getItems.add(FileMenu.QuitMenuItem)
    }
  }

  init()
}