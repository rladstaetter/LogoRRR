package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object CloseAllFilesMenuItem extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseAllFilesMenuItem])

}

class CloseAllFilesMenuItem(fileId: FileId, fileTab: => LogFileTab) extends MenuItem("Close All Files") {
  setId(CloseAllFilesMenuItem.uiNode(fileId).value)
  private val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        t.asInstanceOf[LogFileTab].shutdown()
        Option(t)
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted: _*)
  })

}
