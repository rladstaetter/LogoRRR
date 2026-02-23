package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.{LogFileTab, TabControlEvent}
import javafx.scene.control.{MenuItem, Tab, TabPane}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object CloseOtherFilesMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseOtherFilesMenuItem])

class CloseOtherFilesMenuItem(fileId: FileId, tabPane: TabPane) extends MenuItem("Close Other Files") {
  setId(CloseOtherFilesMenuItem.uiNode(fileId).value)
  setOnAction(_ => {
    tabPane.fireEvent(new TabControlEvent(TabControlEvent.CloseOthers))
  })
}

