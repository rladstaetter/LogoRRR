package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.scene.control.MenuItem

object CloseTabMenuItem extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseTabMenuItem])

}

class CloseTabMenuItem(fileId: FileId, fileTab: => LogFileTab) extends MenuItem("Close") {
  setId(CloseTabMenuItem.uiNode(fileId).value)
  setOnAction(_ => Option(fileTab.getTabPane.getSelectionModel.getSelectedItem).foreach { t =>
    fileTab.shutdown()
    fileTab.getTabPane.getTabs.remove(t)
  })

}
