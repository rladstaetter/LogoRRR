package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala


object CloseRightFilesMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseRightFilesMenuItem])


class CloseRightFilesMenuItem(fileId: FileId, fileTab: => LogFileTab) extends MenuItem("Close Files to the Right") {
  setId(CloseRightFilesMenuItem.uiNode(fileId).value)
  private val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    var deletethem = false
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if t.asInstanceOf[LogFileTab].getFileId == fileTab.getFileId then {
          deletethem = true
          None
        } else {
          if deletethem then {
            t.asInstanceOf[LogFileTab].shutdown()
            Option(t)
          } else {
            None
          }
        }
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted*)
    // reinit context menu since there are no files left on the right side and thus the option should not be shown anymore
    tabPane.getTabs.get(tabPane.getTabs.size() - 1).asInstanceOf[LogFileTab].initContextMenu()

  })

}