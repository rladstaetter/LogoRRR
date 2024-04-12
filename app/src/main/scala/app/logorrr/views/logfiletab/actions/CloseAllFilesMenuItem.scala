package app.logorrr.views.logfiletab.actions

import app.logorrr.io.FileId
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object CloseAllFilesMenuItem extends UiNodeAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseAllFilesMenuItem])

}

class CloseAllFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close All Files") {
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
