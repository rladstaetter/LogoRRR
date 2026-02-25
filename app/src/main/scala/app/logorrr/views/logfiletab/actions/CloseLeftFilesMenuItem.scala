package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.TabControlEvent
import javafx.scene.control.{MenuItem, TabPane}

object CloseLeftFilesMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseLeftFilesMenuItem])

class CloseLeftFilesMenuItem(fileId: FileId, tabPane: TabPane) extends MenuItem("Close Files to the Left") {
  setId(CloseLeftFilesMenuItem.uiNode(fileId).value)
  setOnAction(_ => {
    tabPane.fireEvent(new TabControlEvent(TabControlEvent.CloseLeft))
  })
}
