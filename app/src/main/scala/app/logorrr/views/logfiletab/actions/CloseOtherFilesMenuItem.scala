package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object CloseOtherFilesMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseOtherFilesMenuItem])


class CloseOtherFilesMenuItem(fileId: FileId, fileTab: => LogFileTab) extends MenuItem("Close Other Files"):
  setId(CloseOtherFilesMenuItem.uiNode(fileId).value)
  private val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if t.asInstanceOf[LogFileTab].getFileId == fileTab.getFileId then {
          None
        } else {
          t.asInstanceOf[LogFileTab].shutdown()
          Option(t)
        }
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted*)
    // reinit context menu since only one tab is left
    tabPane.getTabs.get(0).asInstanceOf[LogFileTab].initContextMenu()
  })

