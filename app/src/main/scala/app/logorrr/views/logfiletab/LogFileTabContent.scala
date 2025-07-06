package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.jfxbfr.{ChunkListView, Filter, Fltr}
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.OpsRegion
import app.logorrr.views.ops.time.TimeOpsToolBar
import app.logorrr.views.search.{FiltersToolBar, OpsToolBar}
import app.logorrr.views.text.LogTextView
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.SplitPane
import javafx.scene.layout.{BorderPane, VBox}


class LogFileTabContent(mutLogFileSettings: MutLogFileSettings
                        , val entries: ObservableList[LogEntry]) extends BorderPane {

  // make sure we have a white background for our tabs - see https://github.com/rladstaetter/LogoRRR/issues/188
  setStyle("-fx-background-color: white;")

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](entries)

  // display text to the right
  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // graphical display to the left
  private val chunkListView = mkChunkListView(filteredList, mutLogFileSettings, logTextView.scrollToItem)


  private val blockSizeSlider = {
    val bs = new BlockSizeSlider(mutLogFileSettings.getFileId)
    bs.valueProperty().bindBidirectional(mutLogFileSettings.blockSizeProperty)
    bs
  }

  private val blockPane = {
    val bBp = new BorderPane(chunkListView, blockSizeSlider, null, null, null)
    // vBox.setStyle("-fx-background-color: #b6ff7a;")
    VBox.setVgrow(bBp, javafx.scene.layout.Priority.ALWAYS)
    bBp.setMaxHeight(java.lang.Double.MAX_VALUE)
    bBp
  }

  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  def activeFilters: Seq[Filter] = filtersToolBar.activeFilters()

  def addTailerListener(): Unit = filteredList.addListener(scrollToEndEventListener)

  def removeTailerListener(): Unit = filteredList.removeListener(scrollToEndEventListener)

  val opsToolBar = new OpsToolBar(mutLogFileSettings.getFileId
    , addFilter
    , entries
    , filteredList
    , mutLogFileSettings.blockSizeProperty)

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(mutLogFileSettings, filteredList, removeFilter)
    fbtb.filtersProperty.bind(mutLogFileSettings.filtersProperty)
    fbtb
  }
  val timeOpsToolBar = new TimeOpsToolBar(mutLogFileSettings
    , chunkListView
    , logTextView
    , entries // we write on this list potentially
    , filteredList)
  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, filtersToolBar, timeOpsToolBar)

  private val pane = new SplitPane(blockPane, logTextView)

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

  def addFilter(filter: Fltr): Unit = mutLogFileSettings.filtersProperty.add(filter)

  def removeFilter(filter: Fltr): Unit = mutLogFileSettings.filtersProperty.remove(filter)

  /**
   * Called if a tab is selected
   */
  def recalculateChunkListView(): Unit = chunkListView.recalculateAndUpdateItems("recalculateChunkListView")

  def scrollToActiveElement(): Unit = {
    chunkListView.scrollToActiveChunk()
    logTextView.scrollToActiveLogEntry()
  }

  def mkChunkListView(entries: ObservableList[LogEntry]
                      , mutLogFileSettings: MutLogFileSettings
                      , selectInTextView: LogEntry => Unit
                     ): ChunkListView = {
    new ChunkListView(entries
      , mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.blockSizeProperty
      , mutLogFileSettings.filtersProperty
      , mutLogFileSettings.dividerPositionProperty
      , mutLogFileSettings.firstVisibleTextCellIndexProperty
      , mutLogFileSettings.lastVisibleTextCellIndexProperty
      , selectInTextView)
  }

}
