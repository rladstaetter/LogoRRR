package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala


object CloseRightFilesMenuItem extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseRightFilesMenuItem])

}

class CloseRightFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close Files to the Right") {
  private val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    var deletethem = false
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if (t.asInstanceOf[LogFileTab].fileId == fileTab.fileId) {
          deletethem = true
          None
        } else {
          if (deletethem) {
            t.asInstanceOf[LogFileTab].shutdown()
            Option(t)
          } else {
            None
          }
        }
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted: _*)
    // reinit context menu since there are no files left on the right side and thus the option should not be shown anymore
    tabPane.getTabs.get(tabPane.getTabs.size() - 1).asInstanceOf[LogFileTab].initContextMenu()

  })

}