package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.util.OsUtil
import javafx.scene.control.MenuItem

import java.awt.Desktop

object OpenInFinderMenuItem {
  val menuItemText: String = if (OsUtil.isWin) {
    "Show Log in Explorer"
  } else if (OsUtil.isMac) {
    "Show Log in Finder"
  } else {
    "Show Log ..."
  }
}

class OpenInFinderMenuItem(fileId: FileId) extends MenuItem(OpenInFinderMenuItem.menuItemText) {

  setOnAction(_ => Desktop.getDesktop.open(fileId.asPath.getParent.toFile))

}
