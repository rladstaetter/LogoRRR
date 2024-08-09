package app.logorrr.views.ops.time

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListView
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{Label, ToolBar}

import java.time.format.DateTimeFormatter

object TimeOpsToolBar {

  private def updateFilteredList(filteredList: FilteredList[LogEntry], lower: Long, higher: Long): Unit = {
    filteredList.setPredicate((t: LogEntry) => {
      t.someInstant match {
        case Some(value) =>
          val asMilli = value.toEpochMilli
          lower <= asMilli && asMilli <= higher
        case None => false
      }
    })
  }

}

class TimeOpsToolBar(mutLogFileSettings: MutLogFileSettings
                     , chunkListView: ChunkListView
                     , logEntries: ObservableList[LogEntry]
                     , filteredList: FilteredList[LogEntry]) extends ToolBar {

  val formatter: DateTimeFormatter = mutLogFileSettings.someLogEntrySettingsProperty.get().map(_.dateTimeFormatter).getOrElse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
  val fileId: FileId = mutLogFileSettings.getFileId

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  val timestampSettingsButton = new TimestampSettingsButton(LogoRRRGlobals.getLogFileSettings(fileId), chunkListView, logEntries)

  val lowSlider = new TimerSlider(filteredList)
  lowSlider.setValue(lowSlider.getMin)
  lowSlider.disableProperty().bind(mutLogFileSettings.hasLogEntrySettingBinding.not)
  val highSlider = new TimerSlider(filteredList)
  highSlider.setValue(highSlider.getMax)
  highSlider.disableProperty().bind(mutLogFileSettings.hasLogEntrySettingBinding.not)
  val leftLabel = new Label()
  leftLabel.setText(TimerSlider.format(lowSlider.getValue.longValue(), formatter))
  leftLabel.visibleProperty().bind(mutLogFileSettings.hasLogEntrySettingBinding)
  val rightLabel = new Label()
  rightLabel.setText(TimerSlider.format(highSlider.getValue.longValue(), formatter))
  rightLabel.visibleProperty().bind(mutLogFileSettings.hasLogEntrySettingBinding)
  lowSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue > highSlider.getValue) lowSlider.setValue(highSlider.getValue)
    leftLabel.setText(TimerSlider.format(newValue.longValue, formatter))
    TimeOpsToolBar.updateFilteredList(filteredList, newValue.longValue(), highSlider.getValue.longValue)
  })

  highSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue < lowSlider.getValue) highSlider.setValue(lowSlider.getValue)
    rightLabel.setText(TimerSlider.format(newValue.longValue, formatter))
    TimeOpsToolBar.updateFilteredList(filteredList, lowSlider.getValue.longValue, newValue.longValue())
  })

  getItems.addAll(Seq(timestampSettingsButton, leftLabel, lowSlider, highSlider, rightLabel): _*)

}



