package app.logorrr.views.logfiletab

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.Fs
import app.logorrr.model.LogEntry
import app.logorrr.util._
import app.logorrr.views.LogoRRRAccelerators
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.block.ChunkListView
import app.logorrr.views.ops.OpsRegion
import app.logorrr.views.search.{Filter, FiltersToolBar, Fltr, OpsToolBar}
import app.logorrr.views.text.LogTextView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control._
import javafx.scene.layout._

import java.lang
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{CancellationException, Future}
import scala.util.{Failure, Success}


object LogFileTab {

  private val BackgroundStyle: String =
    """
      |-fx-background-color: WHITE;
      |-fx-border-width: 1px 1px 1px 0px;
      |-fx-border-color: LIGHTGREY;
      |""".stripMargin

  private val BackgroundSelectedStyle: String =
    """
      |-fx-background-color: floralwhite;
      |-fx-border-width: 1px 1px 1px 0px;
      |-fx-border-color: LIGHTGREY;
      |""".stripMargin

}


/**
 * Represents a single 'document' UI approach for a log file.
 *
 * One can view / interact with more than one log file at a time, using tabs here feels quite natural.
 *
 * @param entries report instance holding information of log file to be analyzed
 * */
class LogFileTab(val pathAsString: String
                 , val entries: ObservableList[LogEntry]) extends Tab
  with TimerCode
  with CanLog {

  // lazy since only if autoscroll is set start tailer
  private lazy val logTailer = LogTailer(pathAsString, entries)

  lazy val mutLogFileSettings: MutLogFileSettings = LogoRRRGlobals.getLogFileSettings(pathAsString)

  /** list of search filters to be applied */
  private val filtersListProperty = new SimpleListProperty[Filter](CollectionUtils.mkEmptyObservableList())

  /** split visual view and text view */
  private val splitPane = new SplitPane()

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](entries)

  private val opsToolBar = new OpsToolBar(pathAsString, addFilter, entries, mutLogFileSettings.blockSizeProperty)

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(filteredList, removeFilter)
    fbtb.filtersProperty.bind(filtersListProperty)
    fbtb
  }


  private val opsRegion: OpsRegion = {
    val op = new OpsRegion(opsToolBar, filtersToolBar)
    op
  }

  private val borderedChunkListView = ChunkListView(filteredList, mutLogFileSettings)

  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    borderedChunkListView.scrollTo(borderedChunkListView.getItems.size())
    logTextView.scrollTo(logTextView.getItems.size)
  }

  private def startTailer(): Unit = {
    filteredList.addListener(scrollToEndEventListener)
    logTailer.start()
  }

  private def stopTailer(): Unit = {
    filteredList.removeListener(scrollToEndEventListener)
    logTailer.stop()
  }

  private val autoScrollListener = JfxUtils.onNew[lang.Boolean] {
    b =>
      if (b) {
        startTailer()
      } else {
        stopTailer()
      }
  }

  private val filterChangeListener = {
    def handleFilterChange(change: ListChangeListener.Change[_ <: Fltr]): Unit = {
      while (change.next()) {
        Future {
          LogoRRRGlobals.persist()
        }
      }
    }

    JfxUtils.mkListChangeListener(handleFilterChange)
  }

  private val selectedListener = JfxUtils.onNew[lang.Boolean](b => {
    if (b) {
      setStyle(LogFileTab.BackgroundSelectedStyle)
      /* change active text field depending on visible tab */
      LogoRRRAccelerators.setActiveSearchTextField(opsToolBar.searchTextField)
      LogoRRRAccelerators.setActiveRegexToggleButton(opsToolBar.regexToggleButton)
      repaint()
    } else {
      setStyle(LogFileTab.BackgroundStyle)
    }
  })

  def repaint(): Unit = borderedChunkListView.repaint()


  val repaintChunkListViewListener = JfxUtils.onNew[Number](n => {
    if (n.doubleValue() > 0.1) {
      repaint()
    }
  })

  def init(): Unit = {

    setContextMenu(new ContextMenu(new OpenInFinderMenuItem(pathAsString)))
    setTooltip(new LogFileTabToolTip(pathAsString, entries))

    initBindings()

    // setup split pane before listener initialisation
    splitPane.getItems.addAll(borderedChunkListView, logTextView)

    addListeners()

    setOnSelectionChanged(e => {
      if (isSelected) {
        logTrace("selectionchanged")
        borderedChunkListView.repaint()
      }
    })

    /** don't monitor file anymore if tab is closed, free listeners */
    setOnClosed(_ => {
      shutdown()
      LogoRRRGlobals.removeLogFile(pathAsString)
    })

    if (mutLogFileSettings.isAutoScrollActive) {
      startTailer()
    }


    /** top component for log view */
    setContent(new BorderPane(splitPane, opsRegion, null, null, null))

    // divider.setPosition(mutLogFileSettings.getDividerPosition())

    logTrace(s"Loaded `$pathAsString` with ${entries.size()} entries.")
  }


  private def addListeners(): Unit = {
    borderedChunkListView.addListeners()
    selectedProperty().addListener(selectedListener)
    divider.positionProperty().addListener(repaintChunkListViewListener)

    mutLogFileSettings.autoScrollActiveProperty.addListener(autoScrollListener)
    filtersListProperty.addListener(filterChangeListener)
  }

  private def initBindings(): Unit = {
    filtersListProperty.bind(mutLogFileSettings.filtersProperty)
    //  logVisualView.blockViewPane.blockSizeProperty.bind(opsRegion.opsToolBar.blockSizeProperty)
    textProperty.bind(Bindings.concat(Fs.logFileName(pathAsString)))
  }

  private def removeListeners(): Unit = {
    borderedChunkListView.removeListeners()
    selectedProperty().removeListener(selectedListener)

    divider.positionProperty().removeListener(repaintChunkListViewListener)

    mutLogFileSettings.autoScrollActiveProperty.removeListener(autoScrollListener)
    filtersListProperty.removeListener(filterChangeListener)
  }

  def shutdown(): Unit = {
    if (mutLogFileSettings.isAutoScrollActive) {
      stopTailer()
    }
    removeListeners()
  }

  def addFilter(filter: Filter): Unit = filtersListProperty.add(filter)

  def removeFilter(filter: Filter): Unit = filtersListProperty.remove(filter)

  def activeFilters: Seq[Filter] = filtersToolBar.activeFilters()

  def divider: SplitPane.Divider = splitPane.getDividers.get(0)


}


