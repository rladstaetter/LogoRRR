package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.util.OsUtil
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.MenuItem

import java.awt.Desktop

object OpenInFinderMenuItem extends UiNodeFileIdAware {

  val menuItemText: String = if (OsUtil.isWin) {
    "Show File in Explorer"
  } else if (OsUtil.isMac) {
    "Show File in Finder"
  } else {
    "Show File ..."
  }

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[OpenInFinderMenuItem])

}

class OpenInFinderMenuItem(fileId: FileId) extends MenuItem(OpenInFinderMenuItem.menuItemText) {
  setId(OpenInFinderMenuItem.uiNode(fileId).value)
  setOnAction(_ => {
    new Thread(() => Desktop.getDesktop.open(fileId.asPath.getParent.toFile)).start()
  })

}
