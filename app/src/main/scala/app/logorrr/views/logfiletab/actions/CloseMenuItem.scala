package app.logorrr.views.logfiletab.actions

import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.control.MenuItem

class CloseMenuItem(fileTab: => LogFileTab) extends MenuItem("Close") {

  setOnAction(_ => Option(fileTab.getTabPane.getSelectionModel.getSelectedItem).foreach { t =>
    fileTab.shutdown()
    fileTab.getTabPane.getTabs.remove(t)
  })

}
