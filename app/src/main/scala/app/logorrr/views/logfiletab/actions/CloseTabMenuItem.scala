package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.{LogFileTab, TabControlEvent}
import javafx.scene.control.{MenuItem, TabPane}

object CloseTabMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseTabMenuItem])

class CloseTabMenuItem(fileId: FileId, tabPane: TabPane) extends MenuItem("Close") {
  setId(CloseTabMenuItem.uiNode(fileId).value)
  setOnAction(_ => {
    tabPane.fireEvent(new TabControlEvent(TabControlEvent.CloseSelectedTab))
  })
}

/*
class CloseTabMenuItem(fileId: FileId, fileTab: => LogFileTab) extends MenuItem("Close"):
  setId(CloseTabMenuItem.uiNode(fileId).value)
  setOnAction(_ => Option(fileTab.getTabPane.getSelectionModel.getSelectedItem).foreach { t =>
    fileTab.shutdown()
    fileTab.getTabPane.getTabs.remove(t)
  })
*/
