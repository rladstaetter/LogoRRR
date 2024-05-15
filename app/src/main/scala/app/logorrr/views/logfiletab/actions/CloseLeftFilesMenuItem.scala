package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object CloseLeftFilesMenuItem extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseLeftFilesMenuItem])

}

class CloseLeftFilesMenuItem(fileId: FileId, fileTab: => LogFileTab) extends MenuItem("Close Files to the Left") {
  setId(CloseLeftFilesMenuItem.uiNode(fileId).value)
  private val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    var deletethem = true
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if (t.asInstanceOf[LogFileTab].fileId == fileTab.fileId) {
          deletethem = false
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
    // reinit context menu since there are no files left on the left side and thus the option should not be shown anymore
    tabPane.getTabs.get(0).asInstanceOf[LogFileTab].initContextMenu()

  })

}
