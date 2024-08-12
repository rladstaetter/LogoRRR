package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListView
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.ToolBar

object TimeOpsToolBar {
  private def updateFilteredList(mutLogFileSettings: MutLogFileSettings, filteredList: FilteredList[LogEntry], lower: Long, higher: Long): Unit = {
    //mutLogFileSettings.filtersProperty
    // use filters and also this filter
    mutLogFileSettings == mutLogFileSettings
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

  //val fileId: FileId = mutLogFileSettings.getFileId
  // val formatter: DateTimeFormatter = Option(mutLogFileSettings.getDateTimeFormatter()).getOrElse(TimestampSettings.Default.dateTimeFormatter)

  private val lowSlider = new TimerSlider(mutLogFileSettings.hasLogEntrySettingBinding, "Configure earliest timestamp to be displayed")
  private val highSlider = new TimerSlider(mutLogFileSettings.hasLogEntrySettingBinding, "Configure latest timestamp to be displayed")
  private val leftLabel = new TimestampSliderLabel(mutLogFileSettings, lowSlider)
  private val rightLabel = new TimestampSliderLabel(mutLogFileSettings, highSlider)

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(mutLogFileSettings, chunkListView, logEntries)

  lowSlider.setValue(lowSlider.getMin)
  highSlider.setValue(highSlider.getMax)

  lowSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue > highSlider.getValue) lowSlider.setValue(highSlider.getValue)
    TimeOpsToolBar.updateFilteredList(mutLogFileSettings, filteredList, newValue.longValue(), highSlider.getValue.longValue)
  })

  highSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue < lowSlider.getValue) highSlider.setValue(lowSlider.getValue)
    TimeOpsToolBar.updateFilteredList(mutLogFileSettings, filteredList, lowSlider.getValue.longValue, newValue.longValue())
  })

  getItems.addAll(Seq(timestampSettingsButton, leftLabel, lowSlider, highSlider, rightLabel): _*)

  updateSliderBoundaries()

  private def updateSliderBoundaries(): Unit = {
    TimerSlider.calculateBoundaries(filteredList) match {
      case Some((minInstant, maxInstant)) =>
        lowSlider.setMin(minInstant.toEpochMilli.doubleValue())
        highSlider.setMin(minInstant.toEpochMilli.doubleValue())
        lowSlider.setMax(maxInstant.toEpochMilli.doubleValue())
        highSlider.setMax(maxInstant.toEpochMilli.doubleValue())
        lowSlider.setValue(lowSlider.getMin)
        highSlider.setValue(highSlider.getMax)
        //println("set" + minInstant + "/" + maxInstant)
      case None => //println("none")
    }
  }

}



