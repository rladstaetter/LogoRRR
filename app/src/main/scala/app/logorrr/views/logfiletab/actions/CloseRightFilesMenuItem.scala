package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.TabControlEvent
import javafx.scene.control.{MenuItem, TabPane}


object CloseRightFilesMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseRightFilesMenuItem])

class CloseRightFilesMenuItem(fileId: FileId, tabPane: TabPane) extends MenuItem("Close Files to the Right") {
  setId(CloseRightFilesMenuItem.uiNode(fileId).value)
  setOnAction(_ => {
    tabPane.fireEvent(new TabControlEvent(TabControlEvent.CloseRight))
  })
}
