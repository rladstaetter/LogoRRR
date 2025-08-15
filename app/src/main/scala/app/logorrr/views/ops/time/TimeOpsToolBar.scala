package app.logorrr.views.ops.time

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.ToolBar
import net.ladstatt.util.log.CanLog


class TimeOpsToolBar(mutLogFileSettings: MutLogFileSettings
                     , chunkListView: ChunkListView[LogEntry]
                     , logEntries: ObservableList[LogEntry]
                     , filteredList: FilteredList[LogEntry]) extends ToolBar with CanLog {

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(mutLogFileSettings, chunkListView, logEntries, this)

  def timeRange: TimeRange = TimeUtil.calcTimeInfo(logEntries).getOrElse(TimeRange.defaultTimeRange)

  private lazy val lowerSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_LEFT, "Configure earliest timestamp to be displayed", timeRange)
  private lazy val upperSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_RIGHT, "Configure latest timestamp to be displayed", timeRange)

  private val lowerSlider = lowerSliderVBox.slider
  private val upperSlider = upperSliderVBox.slider

  //  private val replayStackPane = new ReplayStackPane(mutLogFileSettings, logEntries, lowerSlider, upperSlider, logTextView)
  // logTextView == logTextView
  //  private val stopTimeAnimationButton = new StopTimeAnimationButton(mutLogFileSettings, replayStackPane)

  Option(mutLogFileSettings.filteredRangeBinding.get()).map(setSliderPositions).getOrElse(setSliderPositions(timeRange))

  lowerSlider.valueProperty.addListener((_, _, newValue) => updateLowerTimestampSlider(newValue))
  upperSlider.valueProperty.addListener((_, _, newValue) => updateUpperTimestampSlider(newValue))

  getItems.addAll(Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox): _*)

  def initializeRanges() : Unit = {
    val range = timeRange
    lowerSlider.setRange(range)
    lowerSlider.setInstant(range.start)
    upperSlider.setRange(range)
    upperSlider.setInstant(range.end)
    setSliderPositions(range)
  }

  private def setSliderPositions(filterRange: TimeRange): Unit = {
    lowerSlider.setInstant(filterRange.start)
    upperSlider.setInstant(filterRange.end)
  }

  private def updateLowerTimestampSlider(newValue: Number): Unit = {
    if (newValue.doubleValue > upperSlider.getValue) lowerSlider.setValue(upperSlider.getValue)
    mutLogFileSettings.setLowerTimestampValue(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)
  }

  private def updateUpperTimestampSlider(newValue: Number): Unit = {
    if (newValue.doubleValue < lowerSlider.getValue) upperSlider.setValue(lowerSlider.getValue)
    mutLogFileSettings.setUpperTimestampValue(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)
  }

}




