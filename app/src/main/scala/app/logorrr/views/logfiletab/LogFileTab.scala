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
import javafx.event.Event
import javafx.scene.control._
import javafx.scene.layout._

import java.lang
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


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

  /** split visual view and text view */
  private val splitPane = new SplitPane()

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](entries)

  private val opsToolBar = new OpsToolBar(pathAsString, addFilter, entries, filteredList, mutLogFileSettings.blockSizeProperty)

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(filteredList, removeFilter)
    fbtb.filtersProperty.bind(mutLogFileSettings.filtersProperty)
    fbtb
  }


  private val opsRegion: OpsRegion = new OpsRegion(opsToolBar, filtersToolBar)

  // display text to the right
  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // graphical display to the left
  private val chunkListView = ChunkListView(filteredList, mutLogFileSettings, logTextView.selectLogEntry)


  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    chunkListView.scrollTo(chunkListView.getItems.size())
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

  def repaint(): Unit = chunkListView.repaint()


  private val repaintChunkListViewListener = JfxUtils.onNew[Number](n => {
    if (n.doubleValue() > 0.1) {
      repaint()
    }
  })

  def init(): Unit = {


    setTooltip(new LogFileTabToolTip(pathAsString, entries))

    initBindings()

    // setup split pane before listener initialisation
    splitPane.getItems.addAll(chunkListView, logTextView)

    addListeners()

    setOnSelectionChanged(_ => {
      if (isSelected) {
        chunkListView.repaint()
      }
    })

    /** don't monitor file anymore if tab is closed, free listeners */
    setOnCloseRequest((t: Event) => cleanupBeforeClose())

    if (mutLogFileSettings.isAutoScrollActive) {
      startTailer()
    }


    /** top component for log view */
    setContent(new BorderPane(splitPane, opsRegion, null, null, null))

    divider.setPosition(mutLogFileSettings.getDividerPosition())

    // we have to set the context menu in relation to the visibility of the tab itself
    // otherwise those context menu actions can be performed without selecting the tab
    // which is kind of confusing
    selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean] {
      case java.lang.Boolean.TRUE =>
        // getTabPane can be null on initialisation
        Option(getTabPane).foreach(_ => setContextMenu(mkContextMenu()))
      case java.lang.Boolean.FALSE => setContextMenu(null)
    })

    logTrace(s"Loaded `$pathAsString` with ${entries.size()} entries.")
  }


  private def mkContextMenu(): ContextMenu = {
    val openInFinderMenuItem = new OpenInFinderMenuItem(pathAsString)
    val closeMenuItem = new CloseMenuItem(this)
    val closeOtherFilesMenuItem = new CloseOtherFilesMenuItem(this)
    val closeAllFilesMenuItem = new CloseAllFilesMenuItem(this)

    // close left/right is not always shown. see https://github.com/rladstaetter/LogoRRR/issues/159
    val leftRightCloser =
      if (getTabPane.getTabs.size() == 1) {
        Seq()
        // current tab is the first one, show only 'right'
      } else if (getTabPane.getTabs.indexOf(this) == 0) {
        Seq(new CloseRightFilesMenuItem(this))
        // we are at the end of the list
      } else if (getTabPane.getTabs.indexOf(this) == getTabPane.getTabs.size - 1) {
        Seq(new CloseLeftFilesMenuItem(this))
        // we are somewhere in between, show both options
      } else {
        Seq(new CloseLeftFilesMenuItem(this), new CloseRightFilesMenuItem(this))
      }

    val items = Seq(closeMenuItem
      , closeOtherFilesMenuItem
      , closeAllFilesMenuItem) ++ leftRightCloser ++ Seq(openInFinderMenuItem)

    val menu = new ContextMenu()
    menu.getItems.addAll(items: _*)
    menu
  }

  def cleanupBeforeClose(): Unit = {
    shutdown()
    LogoRRRGlobals.removeLogFile(pathAsString)
  }

  private def addListeners(): Unit = {
    chunkListView.addListeners()
    selectedProperty().addListener(selectedListener)
    divider.positionProperty().addListener(repaintChunkListViewListener)

    mutLogFileSettings.autoScrollActiveProperty.addListener(autoScrollListener)
    mutLogFileSettings.filtersProperty.addListener(filterChangeListener)
  }

  private def initBindings(): Unit = {
    filtersListProperty.bind(mutLogFileSettings.filtersProperty)
    //  logVisualView.blockViewPane.blockSizeProperty.bind(opsRegion.opsToolBar.blockSizeProperty)
    textProperty.bind(Bindings.concat(Fs.logFileName(pathAsString)))
  }

  private def removeListeners(): Unit = {
    chunkListView.removeListeners()
    selectedProperty().removeListener(selectedListener)

    mutLogFileSettings.autoScrollActiveProperty.removeListener(autoScrollListener)
    mutLogFileSettings.filtersProperty.removeListener(filterChangeListener)
  }

  def shutdown(): Unit = {
    if (mutLogFileSettings.isAutoScrollActive) {
      stopTailer()
    }
    removeListeners()
  }

  def addFilter(filter: Filter): Unit = mutLogFileSettings.filtersProperty.add(filter)

  def removeFilter(filter: Filter): Unit = mutLogFileSettings.filtersProperty.remove(filter)

  def activeFilters: Seq[Filter] = filtersToolBar.activeFilters()

  def divider: SplitPane.Divider = splitPane.getDividers.get(0)


}


