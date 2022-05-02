package app.logorrr.views

import app.logorrr.model.LogEntryInstantFormat
import app.logorrr.util.JfxUtils
import javafx.scene.Scene
import javafx.scene.control.{Button, ToolBar}
import javafx.stage.{Modality, Stage}


/* container to host settings button, positioned to the right of the application / separate for each log view */
class SettingsToolBar(pathAsString: String) extends ToolBar {
  private val settingsButton = new Button("Settings")
  settingsButton.setOnAction(_ => {
    val stage = new Stage()
    stage.initModality(Modality.APPLICATION_MODAL)
    stage.setTitle(s"Settings for ${pathAsString}")
    val scene = new Scene(new SettingsBorderPane(pathAsString, updateLogEntrySetting, JfxUtils.closeStage(stage)), 950, 37)
    stage.setScene(scene)
    stage.setOnCloseRequest(_ => stage.close())
    stage.showAndWait()
  })
  getItems.add(settingsButton)

  def updateLogEntrySetting(logEntrySetting: LogEntryInstantFormat): Unit = {
    ???
    /*
    val updatedLogFileDefinitions = lrd.copy(someLogEntrySetting = Option(logEntrySetting))
    LogoRRRGlobals.updateLogFile()
    LogoRRRGlobals.updateRecentFileSettings(rf => rf.update(updatedLogFileDefinitions))

     */
  }
}
