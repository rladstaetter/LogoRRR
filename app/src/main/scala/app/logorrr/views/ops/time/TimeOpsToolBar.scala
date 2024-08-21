package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListView
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.ToolBar


class TimeOpsToolBar(mutLogFileSettings: MutLogFileSettings
                     , chunkListView: ChunkListView
                     , logEntries: ObservableList[LogEntry]
                     , filteredList: FilteredList[LogEntry]) extends ToolBar {

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(mutLogFileSettings, chunkListView, logEntries, this)

  val lowerSliderVBox = new SliderVBox(mutLogFileSettings, "Configure earliest timestamp to be displayed", Pos.CENTER_LEFT)
  val upperSliderVBox = new SliderVBox(mutLogFileSettings, "Configure latest timestamp to be displayed", Pos.CENTER_RIGHT)

  private val lowerSlider = lowerSliderVBox.slider
  private val upperSlider = upperSliderVBox.slider

  private val replayButton = new ReplayButton(mutLogFileSettings, logEntries, lowerSlider, upperSlider)

  private val stopTimeAnimationButton = new StopTimeAnimationButton(mutLogFileSettings, replayButton)

  lowerSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue > upperSlider.getValue) lowerSlider.setValue(upperSlider.getValue)
    mutLogFileSettings.setLowerTimestamp(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)
  })

  upperSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue < lowerSlider.getValue) upperSlider.setValue(lowerSlider.getValue)
    mutLogFileSettings.setUpperTimestamp(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)
  })

  getItems.addAll(Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox, replayButton, stopTimeAnimationButton): _*)

  updateSliderBoundaries()

  def updateSliderBoundaries(): Unit = {
    TimerSlider.calcTimeInfo(filteredList) match {
      case Some(TimeInfo(minInstant, maxInstant)) =>
        lowerSlider.setBoundsAndValue(minInstant, maxInstant, minInstant.toEpochMilli.doubleValue)
        upperSlider.setBoundsAndValue(minInstant, maxInstant, maxInstant.toEpochMilli.doubleValue)
      case None =>
    }
  }

}



