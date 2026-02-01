package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.time.{SliderVBox, TimeRange, TimeUtil, TimestampSettingsButton}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import net.ladstatt.util.os.OsUtil


class TimestampSettingsRegion(mutLogFileSettings: MutLogFileSettings
                              , chunkListView: ChunkListView[LogEntry]
                              , logEntries: ObservableList[LogEntry]
                              , filteredList: FilteredList[LogEntry]):

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(mutLogFileSettings, chunkListView, logEntries, this)


  private lazy val lowerSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_LEFT, "Configure earliest timestamp to be displayed", timeRange)
  private lazy val upperSliderVBox = new SliderVBox(mutLogFileSettings, Pos.CENTER_RIGHT, "Configure latest timestamp to be displayed", timeRange)

  private val lowerSlider = lowerSliderVBox.slider
  private val upperSlider = upperSliderVBox.slider

  Option(mutLogFileSettings.filteredRangeBinding.get()).map(setSliderPositions).getOrElse(setSliderPositions(timeRange))

  lowerSlider.valueProperty.addListener((_, _, newValue) => updateLowerTimestampSlider(newValue))
  upperSlider.valueProperty.addListener((_, _, newValue) => updateUpperTimestampSlider(newValue))


  def timeRange: TimeRange = TimeUtil.calcTimeInfo(logEntries).getOrElse(TimeRange.defaultTimeRange)

  def initializeRanges(): Unit =
    val range = timeRange
    lowerSlider.setRange(range)
    lowerSlider.setInstant(range.start)
    upperSlider.setRange(range)
    upperSlider.setInstant(range.end)
    setSliderPositions(range)

  private def setSliderPositions(filterRange: TimeRange): Unit =
    lowerSlider.setInstant(filterRange.start)
    upperSlider.setInstant(filterRange.end)

  private def updateLowerTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue > upperSlider.getValue then lowerSlider.setValue(upperSlider.getValue)
    mutLogFileSettings.setLowerTimestampValue(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)

  private def updateUpperTimestampSlider(newValue: Number): Unit =
    if newValue.doubleValue < lowerSlider.getValue then upperSlider.setValue(lowerSlider.getValue)
    mutLogFileSettings.setUpperTimestampValue(newValue.longValue())
    mutLogFileSettings.updateActiveFilter(filteredList)


  val items: Seq[Node] = Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox)
