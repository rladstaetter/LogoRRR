package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListView
import app.logorrr.views.ops.OpsRegion
import app.logorrr.views.search.{Filter, FiltersToolBar, OpsToolBar}
import app.logorrr.views.text.LogTextView
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane

class LogFileTabContent(mutLogFileSettings: MutLogFileSettings
                        , val entries: ObservableList[LogEntry]) extends BorderPane {

  // make sure we have a white background for our tabs - see https://github.com/rladstaetter/LogoRRR/issues/188
  setStyle("-fx-background-color: white;")

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](entries)

  // display text to the right
  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // graphical display to the left
  private val chunkListView = ChunkListView(filteredList, mutLogFileSettings, logTextView.scrollToItem)

  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  def activeFilters: Seq[Filter] = filtersToolBar.activeFilters()

  def addTailerListener(): Unit = filteredList.addListener(scrollToEndEventListener)

  def removeTailerListener(): Unit = filteredList.removeListener(scrollToEndEventListener)

  val opsToolBar = new OpsToolBar(mutLogFileSettings.getFileId, addFilter, entries, filteredList, mutLogFileSettings.blockSizeProperty)

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(filteredList, removeFilter)
    fbtb.filtersProperty.bind(mutLogFileSettings.filtersProperty)
    fbtb
  }

  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, filtersToolBar)

  private val pane = new SplitPane(chunkListView, logTextView)


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
    chunkListView.removeListeners()
  }

  def addFilter(filter: Filter): Unit = mutLogFileSettings.filtersProperty.add(filter)

  def removeFilter(filter: Filter): Unit = mutLogFileSettings.filtersProperty.remove(filter)

  /**
   * Called if a tab is selected
   */
  def recalculateChunkListView(): Unit = chunkListView.recalculateAndUpdateItems("recalculateChunkListView")

  def scrollToActiveElement(): Unit = {
    chunkListView.scrollToActiveChunk()
    logTextView.scrollToActiveLogEntry()
  }

}
