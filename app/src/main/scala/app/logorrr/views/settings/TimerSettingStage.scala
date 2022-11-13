package app.logorrr.views.settings

import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.{JfxUtils, LogFileUtil}
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.{Modality, Stage}

class TimerSettingStage(pathAsString: String
                        , updateLogEntrySetting: LogEntryInstantFormat => Unit
                        , logEntriesToDisplay: ObservableList[LogEntry]) extends Stage {
  val width = 1100
  val height = 300
  initModality(Modality.APPLICATION_MODAL)
  setTitle(s"Timer settings for ${LogFileUtil.logFileName(pathAsString)}")
  val scene = new Scene(new TimerSettingsBorderPane(pathAsString, logEntriesToDisplay, updateLogEntrySetting, JfxUtils.closeStage(this)), width, height)
  setScene(scene)
  setOnCloseRequest(_ => this.close())
}
