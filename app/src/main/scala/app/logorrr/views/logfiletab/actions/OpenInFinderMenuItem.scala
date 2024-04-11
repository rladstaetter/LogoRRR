package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.util.OsUtil
import javafx.scene.control.MenuItem

import java.awt.Desktop

object OpenInFinderMenuItem {
  val menuItemText: String = if (OsUtil.isWin) {
    "Show File in Explorer"
  } else if (OsUtil.isMac) {
    "Show File in Finder"
  } else {
    "Show File ..."
  }
}

class OpenInFinderMenuItem(fileId: FileId) extends MenuItem(OpenInFinderMenuItem.menuItemText) {

  setOnAction(_ => {
    val directoryToOpen = fileId.asPath.getParent.toFile
    new Thread(new Runnable {
      override def run(): Unit = Desktop.getDesktop.open(directoryToOpen)
    }).start()
  })

}
