package app.logorrr.views

import app.logorrr.conf.SettingsIO
import app.logorrr.model.LogReportDefinition
import javafx.scene.control._

import scala.jdk.CollectionConverters._


class OpsToolBar(logReportTab: LogReportTab) extends ToolBar {

  val initialText = "<enter search string>"
  val searchTextField = new TextField()
  searchTextField.setPrefWidth(200)
  searchTextField.setPromptText(initialText)

  val colorPicker = new ColorPicker()
  val add = new Button("search")
  add.setOnAction(_ => {
    val filter = new Filter(searchTextField.getText, colorPicker.getValue.toString)
    searchTextField.setText("")
    logReportTab.addFilter(filter)
    // update settings
    val definition: LogReportDefinition = logReportTab.logReport.logFileDefinition
    val updatedDefinition = definition.copy(filters = logReportTab.filtersListProperty.asScala.toSeq)
    SettingsIO.updateRecentFileSettings(rf => rf.update(updatedDefinition))
  })

  getItems.addAll(searchTextField, colorPicker, add)
}
