package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.SearchTerm
import app.logorrr.views.ops.OpsRegion
import app.logorrr.views.ops.time.TimeOpsToolBar
import app.logorrr.views.search.{FiltersToolBar, OpsToolBar}
import app.logorrr.views.text.LogTextView
import javafx.beans.property.Property
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{ListView, Slider, SplitPane}
import javafx.scene.layout.{BorderPane, VBox}

object LogFileTabContent {

  /** wire pane and slider together */
  def mkPane(listView: ListView[_], slider: Slider, boundProp: Property[Number]): BorderPane = {
    val bBp = new BorderPane(listView, slider, null, null, null)
    slider.valueProperty().bindBidirectional(boundProp)
    VBox.setVgrow(bBp, javafx.scene.layout.Priority.ALWAYS)
    bBp.setMaxHeight(java.lang.Double.MAX_VALUE)
    bBp
  }

}

class LogFileTabContent(mutLogFileSettings: MutLogFileSettings
                        , val entries: ObservableList[LogEntry]) extends BorderPane {

  // make sure we have a white background for our tabs - see https://github.com/rladstaetter/LogoRRR/issues/188
  setStyle("-fx-background-color: white;")

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](entries)

  // display text to the right
  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // graphical display to the left
  private val chunkListView = LogoRRRChunkListView(filteredList, mutLogFileSettings, logTextView.scrollToItem, widthProperty)

  private val textPane = LogFileTabContent.mkPane(logTextView, new TextSizeSlider(mutLogFileSettings.getFileId), mutLogFileSettings.fontSizeProperty)

  private val chunkPane = LogFileTabContent.mkPane(chunkListView, new BlockSizeSlider(mutLogFileSettings.getFileId), mutLogFileSettings.blockSizeProperty)


  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  def activeFilters: Seq[SearchTerm] = filtersToolBar.activeFilters()

  def addTailerListener(): Unit = filteredList.addListener(scrollToEndEventListener)

  def removeTailerListener(): Unit = filteredList.removeListener(scrollToEndEventListener)

  val opsToolBar = new OpsToolBar(mutLogFileSettings.getFileId
    , mutLogFileSettings.filtersProperty.add(_)
    , entries
    , filteredList
    , mutLogFileSettings.blockSizeProperty)

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(mutLogFileSettings, filteredList, mutLogFileSettings.filtersProperty.remove(_))
    fbtb.filtersProperty.bind(mutLogFileSettings.filtersProperty)
    fbtb
  }
  val timeOpsToolBar = new TimeOpsToolBar(mutLogFileSettings
    , chunkListView
    , entries
    , filteredList)

  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, filtersToolBar, timeOpsToolBar)

  private val pane = new SplitPane(chunkPane, textPane)

  def init(): Unit = {
    divider.setPosition(mutLogFileSettings.getDividerPosition)

    setTop(opsRegion)
    setCenter(pane)

    logTextView.init()
    chunkListView.init()

  }


  private def divider: SplitPane.Divider = pane.getDividers.get(0)

  def getDividerPosition: Double = divider.getPosition

  def removeListeners(): Unit = {
    logTextView.removeListeners()
    chunkListView.removeListeners()
  }

  /**
   * Called if a tab is selected
   */
  def recalculateChunkListView(): Unit = chunkListView.recalculateAndUpdateItems()

  def scrollToActiveElement(): Unit = {
    chunkListView.scrollToActiveChunk()
    logTextView.scrollToActiveLogEntry()
  }

}




