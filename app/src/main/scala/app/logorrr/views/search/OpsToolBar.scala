package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.MutableSearchTerm
import app.logorrr.views.autoscroll.AutoScrollCheckBox
import app.logorrr.views.ops.time.{SliderVBox, TimeRange, TimeUtil, TimestampSettingsButton}
import app.logorrr.views.ops.{ClearLogButton, CopyLogButton}
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}
import net.ladstatt.util.os.OsUtil


object OpsToolBar {


}

/**
 * Groups search ui widgets together.
 *
 * @param addFilterFn filter function which results from user interaction with SearchToolbar
 */
class OpsToolBar(fileId: FileId
                 , mutLogFileSettings: MutLogFileSettings
                 , chunkListView: ChunkListView[LogEntry]
                 , addFilterFn: MutableSearchTerm => Unit
                 , logEntries: ObservableList[LogEntry]
                 , filteredList: FilteredList[LogEntry]
                 , val sizeProperty: SimpleIntegerProperty) extends ToolBar {

  setMaxHeight(Double.PositiveInfinity)

  // TODO fix this; not really elegant
  setStyle("""-fx-padding: 0px 0px 0px 4px;""")
  val w = 380
  private val macWidth: Int = w
  private val winWidth: Int = w + 2
  private val linuxWidth: Int = w + 2
  val width: Int = OsUtil.osFun(winWidth, macWidth, linuxWidth) // different layouts (may be dependent on font size renderings?)
  setMinWidth(width)

  /** control which enables selecting color for a search tag */
  private val colorPicker = new SearchColorPicker(fileId)


  /** textfield to enter search queries */
  val searchTextField = new SearchTextField(fileId)

  private val searchButton = new SearchButton(fileId, searchTextField, colorPicker, addFilterFn)

  private val autoScrollCheckBox = new AutoScrollCheckBox(fileId)

  private val clearLogButton = new ClearLogButton(fileId, logEntries)

  private val copySelectionButton = new CopyLogButton(fileId, filteredList)


  def execSearchOnHitEnter(event: KeyEvent): Unit = {
    if (event.getCode == KeyCode.ENTER) {
      searchButton.fire()
    }
  }

  searchTextField.setOnKeyPressed(execSearchOnHitEnter)
  colorPicker.setOnKeyPressed(execSearchOnHitEnter)

  val searchItems: Seq[Control] = Seq[Control](searchTextField, colorPicker, searchButton)

  val otherItems: Seq[Node] = {
    Seq(autoScrollCheckBox, clearLogButton, copySelectionButton)
  }


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


  def initializeRanges(): Unit = {
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

  private val nodes: Seq[Node] = Seq(timestampSettingsButton, lowerSliderVBox, upperSliderVBox)

  getItems.addAll(searchItems ++ otherItems ++ nodes: _*)

}
