package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.clv.ChunkListView
import app.logorrr.model.LogEntry
import app.logorrr.views.text.LogTextView
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control.ToolBar


class TimeOpsToolBar(mutLogFileSettings: MutLogFileSettings
                     , chunkListView: ChunkListView[LogEntry]
                     , logTextView: LogTextView
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

 //  private val replayStackPane = new ReplayStackPane(mutLogFileSettings, logEntries, lowerSlider, upperSlider, logTextView)
  logTextView == logTextView
//  private val stopTimeAnimationButton = new StopTimeAnimationButton(mutLogFileSettings, replayStackPane)

  lowerSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue > upperSlider.getValue) lowerSlider.setValue(upperSlider.getValue)
    mutLogFileSettings.setLowerTimestampValue(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)
  })

  upperSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue < lowerSlider.getValue) upperSlider.setValue(lowerSlider.getValue)
    mutLogFileSettings.setUpperTimestampValue(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)
  })

  getItems.addAll(Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox): _*)

//  getItems.addAll(Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox, replayStackPane, stopTimeAnimationButton): _*)

  updateSliderBoundaries()

  def updateSliderBoundaries(): Unit = {
    TimeUtil.calcTimeInfo(filteredList) match {
      case Some(TimeInfo(minInstant, maxInstant)) =>
        lowerSlider.setBoundsAndValue(minInstant, maxInstant, minInstant.toEpochMilli.doubleValue)
        upperSlider.setBoundsAndValue(minInstant, maxInstant, maxInstant.toEpochMilli.doubleValue)
      case None =>
    }
  }

}



