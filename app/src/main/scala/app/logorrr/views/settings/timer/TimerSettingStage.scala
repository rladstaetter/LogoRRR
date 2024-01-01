package app.logorrr.views.settings.timer

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.JfxUtils
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.{Modality, Stage}

object TimerSettingStage {

  val width = 1100
  val height = 300

}

class TimerSettingStage(settings: MutLogFileSettings
                        , updateLogEntrySetting: LogEntryInstantFormat => Unit
                        , logEntriesToDisplay: ObservableList[LogEntry]) extends Stage {

  private val timerSettingsBorderPane = new TimerSettingsBorderPane(settings, logEntriesToDisplay, updateLogEntrySetting, JfxUtils.closeStage(this))
  val scene = new Scene(timerSettingsBorderPane, TimerSettingStage.width, TimerSettingStage.height)
  initModality(Modality.APPLICATION_MODAL)
  setTitle(s"Timer settings for ${settings.getFileId.fileName}")
  setScene(scene)
  setOnCloseRequest(_ => this.close())
}
