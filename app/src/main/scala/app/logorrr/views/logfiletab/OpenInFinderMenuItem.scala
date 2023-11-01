package app.logorrr.views.logfiletab

import app.logorrr.util.OsUtil
import javafx.scene.control.MenuItem

import java.awt.Desktop
import java.nio.file.Paths

class OpenInFinderMenuItem(pathAsString: String) extends MenuItem(if (OsUtil.isWin) {
  "Show Log in Explorer"
} else if (OsUtil.isMac) {
  "Show Log in Finder"
} else {
  "Show Log ..."
}) {

  setOnAction(_ => Desktop.getDesktop.open(Paths.get(pathAsString).getParent.toFile))

}
