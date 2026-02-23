package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.TabControlEvent
import javafx.scene.control.{MenuItem, TabPane}
import net.ladstatt.util.os.OsUtil

import java.awt.Desktop

object OpenInFinderMenuItem extends UiNodeFileIdAware:

  val menuItemText: String = if OsUtil.isWin then
    "Show File in Explorer"
  else if OsUtil.isMac then
    "Show File in Finder"
  else
    "Show File ..."

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[OpenInFinderMenuItem])

class OpenInFinderMenuItem(fileId: FileId, tabPane: TabPane) extends MenuItem(OpenInFinderMenuItem.menuItemText):
  setId(OpenInFinderMenuItem.uiNode(fileId).value)
  setOnAction(_ => {
    tabPane.fireEvent(new TabControlEvent(TabControlEvent.OpenInFinder))
  })

