package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListView
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.ToolBar


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
  private val timestampSettingsButton = new TimestampSettingsButton(mutLogFileSettings, chunkListView, logEntries, this)

  lowSlider.setValue(lowSlider.getMin)
  highSlider.setValue(highSlider.getMax)

  lowSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue > highSlider.getValue) lowSlider.setValue(highSlider.getValue)
    val lowValue = newValue.longValue()
    val highValue = highSlider.getValue.longValue
    mutLogFileSettings.setLowerTimestamp(lowValue)
    mutLogFileSettings.setUpperTimestamp(highValue)
    mutLogFileSettings.updateActiveFilter(filteredList)
    //    TimeOpsToolBar.updateFilteredList(mutLogFileSettings, filteredList, lowValue, highValue)
  })

  highSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue < lowSlider.getValue) highSlider.setValue(lowSlider.getValue)
    val lowValue = lowSlider.getValue.longValue
    val highValue = newValue.longValue()
    mutLogFileSettings.setLowerTimestamp(lowValue)
    mutLogFileSettings.setUpperTimestamp(highValue)
    mutLogFileSettings.updateActiveFilter(filteredList)
  })

  getItems.addAll(Seq(timestampSettingsButton, leftLabel, lowSlider, highSlider, rightLabel): _*)

  updateSliderBoundaries()

  def updateSliderBoundaries(): Unit = {
    TimerSlider.calculateBoundaries(filteredList) match {
      case Some((minInstant, maxInstant)) =>
        lowSlider.setMin(minInstant.toEpochMilli.doubleValue())
        highSlider.setMin(minInstant.toEpochMilli.doubleValue())
        lowSlider.setMax(maxInstant.toEpochMilli.doubleValue())
        highSlider.setMax(maxInstant.toEpochMilli.doubleValue())
        lowSlider.setValue(lowSlider.getMin)
        highSlider.setValue(highSlider.getMax)
        println("set" + minInstant + "/" + maxInstant)
      case None => //println("none")
    }
  }

}



