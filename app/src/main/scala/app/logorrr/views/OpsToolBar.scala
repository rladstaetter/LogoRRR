package app.logorrr.views

import app.logorrr.conf.SettingsIO
import app.logorrr.model.LogReportDefinition
import javafx.scene.control._

import scala.jdk.CollectionConverters._

object SearchOp {

  class SearchTextField extends TextField {
    setPrefWidth(200)
    setPromptText("<enter search string>")
  }

  class SearchButton(searchTextField: SearchTextField
                     , colorPicker: ColorPicker
                     , logReportTab: LogReportTab) extends Button {
    setText("search")

    setOnAction(_ => {
      val filter = new Filter(searchTextField.getText, colorPicker.getValue.toString)
      searchTextField.setText("")
      logReportTab.addFilter(filter)
      // update settings
      val definition: LogReportDefinition = logReportTab.logReport.logFileDefinition
      val updatedDefinition = definition.copy(filters = logReportTab.filtersListProperty.asScala.toSeq)
      SettingsIO.updateRecentFileSettings(rf => rf.update(updatedDefinition))
    })

  }

}

class OpsToolBar(logReportTab: LogReportTab) extends ToolBar {

  val searchTextField = new SearchOp.SearchTextField
  val colorPicker = new ColorPicker()
  val searchButton = new SearchOp.SearchButton(searchTextField, colorPicker, logReportTab)

  getItems.addAll(searchTextField, colorPicker, searchButton)
}
