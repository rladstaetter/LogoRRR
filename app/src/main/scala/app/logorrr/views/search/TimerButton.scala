package app.logorrr.views.search

import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.SettingsBorderPane
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.control.{Button, Tooltip}
import javafx.stage.{Modality, Stage}
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

case class TimerButton(pathAsString: String, logEntries: ObservableList[LogEntry])
  extends Button
    with CanLog {
  setGraphic(new FontIcon(FontAwesomeRegular.CLOCK))
  setTooltip(new Tooltip("configure time format"))

  setOnAction(_ => {
    val stage = new Stage()
    stage.initModality(Modality.APPLICATION_MODAL)
    stage.setTitle(s"Settings for ${pathAsString}")
    val scene = new Scene(new SettingsBorderPane(pathAsString, updateLogEntrySetting, JfxUtils.closeStage(stage)), 950, 37)
    stage.setScene(scene)
    stage.setOnCloseRequest(_ => stage.close())
    stage.showAndWait()
  })

  def updateLogEntrySetting(leif: LogEntryInstantFormat): Unit = {
    logTrace(leif.toString)
  }
}
