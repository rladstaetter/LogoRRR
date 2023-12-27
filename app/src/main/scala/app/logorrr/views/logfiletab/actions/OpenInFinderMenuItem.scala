package app.logorrr.views.logfiletab.actions

import app.logorrr.util.OsUtil
import javafx.scene.control.MenuItem

import java.awt.Desktop
import java.nio.file.Paths

object OpenInFinderMenuItem {
  val menuItemText: String = if (OsUtil.isWin) {
    "Show Log in Explorer"
  } else if (OsUtil.isMac) {
    "Show Log in Finder"
  } else {
    "Show Log ..."
  }
}

class OpenInFinderMenuItem(pathAsString: String) extends MenuItem(OpenInFinderMenuItem.menuItemText) {

  setOnAction(_ => Desktop.getDesktop.open(Paths.get(pathAsString).getParent.toFile))

}
