package app.logorrr.views

import app.logorrr.model.LogReportDefinition
import javafx.application.HostServices
import javafx.scene.Scene
import javafx.scene.control.{Button, ToolBar}
import javafx.stage.{Modality, Stage}


/* container to host settings button, positioned to the right of the application / separate for each log view */
class SettingsToolBar(hostServices: HostServices
                      , lrd: LogReportDefinition) extends ToolBar {
  private val settingsButton = new Button("Settings")
  settingsButton.setOnAction(_ => {
    val stage = new Stage()
    stage.initModality(Modality.APPLICATION_MODAL)
    stage.setTitle(s"Settings for ${lrd.path.getFileName.toString}")
    val scene = new Scene(new SettingsScreen(hostServices, lrd), 800, 37)
    stage.setScene(scene)
    stage.setOnCloseRequest(_ => stage.close())
    stage.showAndWait()
  })
  getItems.add(settingsButton)
}
