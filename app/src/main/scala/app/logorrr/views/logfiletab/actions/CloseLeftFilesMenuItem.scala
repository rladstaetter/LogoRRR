package app.logorrr.views.logfiletab.actions

import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.control.{MenuItem, Tab}

import scala.jdk.CollectionConverters.CollectionHasAsScala

class CloseLeftFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close Files to the Left") {
  private val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    var deletethem = true
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if (t.asInstanceOf[LogFileTab].pathAsString == fileTab.pathAsString) {
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
  })

}
