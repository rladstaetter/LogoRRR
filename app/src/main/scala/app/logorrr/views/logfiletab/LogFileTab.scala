package app.logorrr.views.logfiletab

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{BlockSettings, LogoRRRGlobals}
import app.logorrr.model.LogEntry
import app.logorrr.util._
import app.logorrr.views.LogoRRRAccelerators
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.ops.OpsRegion
import app.logorrr.views.search.{Filter, FiltersToolBar, Fltr, OpsToolBar}
import app.logorrr.views.text.LogTextView
import app.logorrr.views.visual.LogVisualView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control._
import javafx.scene.layout._

import java.awt.Desktop
import java.lang
import java.nio.file.Paths
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

  def apply(pathAsString: String, logEntries: ObservableList[LogEntry]): LogFileTab = {
    val logFileTab = new LogFileTab(pathAsString, logEntries)

    /** activate invalidation listener on filtered list */
    logFileTab.init()
    logFileTab
  }

  private def mkOpenInFinderItem(pathAsString: String): MenuItem = {
    val text = if (OsUtil.isWin) {
      "Show Log in Explorer"
    } else if (OsUtil.isMac) {
      "Show Log in Finder"
    } else {
      "Show Log ..."
    }
    val mi = new MenuItem(text)
    mi.setOnAction(e => {
      Desktop.getDesktop.open(Paths.get(pathAsString).getParent.toFile)
    })
    mi
  }
}

/**
 * Represents a single 'document' UI approach for a log file.
 *
 * One can view / interact with more than one log file at a time, using tabs here feels quite natural.
 *
 * @param logEntries report instance holding information of log file to be analyzed
 * */
class LogFileTab(val pathAsString: String
                 , val logEntries: ObservableList[LogEntry])
  extends Tab with TimerCode with CanLog {

  val openInFinderItem = LogFileTab.mkOpenInFinderItem(pathAsString)

  val cm = new ContextMenu(openInFinderItem)
  setContextMenu(cm)

  // lazy since only if autoscroll is set start tailer
  private lazy val logTailer = LogTailer(pathAsString, logEntries)

  lazy val mutLogFileSettings: MutLogFileSettings = LogoRRRGlobals.getLogFileSettings(pathAsString)

  /** list of search filters to be applied */
  private val filtersListProperty = new SimpleListProperty[Filter](CollectionUtils.mkEmptyObservableList())

  /** split visual view and text view */
  private val splitPane = new SplitPane()

  /** list which holds all entries, default to display all (can be changed via buttons) */
  private val filteredList = new FilteredList[LogEntry](logEntries)

  private val opsToolBar = {
    val op = new OpsToolBar(pathAsString, addFilter, logEntries)
    op.blockSizeProperty.set(mutLogFileSettings.blockWidthSettingsProperty.get())
    op
  }

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(filteredList, removeFilter)
    fbtb.filtersProperty.bind(filtersListProperty)
    fbtb
  }

  def activeFilters: Seq[Filter] = filtersToolBar.activeFilters()

  private val opsRegion: OpsRegion = {
    val op = new OpsRegion(opsToolBar, filtersToolBar)
    op
  }

  private lazy val logVisualView = {
    val lvv = new LogVisualView(mutLogFileSettings.selectedLineNumberProperty
      , mutLogFileSettings.filtersProperty
      , filteredList
      , mutLogFileSettings.blockWidthSettingsProperty)
    lvv.visibleProperty().bind(selectedProperty())
    lvv
  }

  private val logTextView = new LogTextView(mutLogFileSettings, filteredList)

  // start listener declarations
  private lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
    logVisualView.scrollToEnd()
    logTextView.scrollToEnd()
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

  /* change active text field depending on visible tab */
  private val selectedListener = JfxUtils.onNew[lang.Boolean](b => {
    if (b) {
      setStyle(LogFileTab.BackgroundSelectedStyle)
      LogoRRRAccelerators.setActiveSearchTextField(opsToolBar.searchTextField)
      LogoRRRAccelerators.setActiveRegexToggleButton(opsToolBar.regexToggleButton)
    } else {
      setStyle(LogFileTab.BackgroundStyle)
    }
  })

  private val dividerPositionListener = JfxUtils.onNew {
    t1: Number => LogoRRRGlobals.setDividerPosition(pathAsString, t1.doubleValue())
  }

  def init(): Unit = {

    initBindings()

    // setup split pane before listener initialisation
    splitPane.getItems.addAll(logVisualView, logTextView)
    setDivider(mutLogFileSettings.getDividerPosition())

    addListeners()

    /** don't monitor file anymore if tab is closed, free invalidation listeners */
    setOnClosed(_ => shutdown())

    setTooltip(new LogFileTabToolTip(pathAsString, logEntries))

    if (mutLogFileSettings.isAutoScrollActive) {
      startTailer()
    }

    /** top component for log view */
    setContent(new BorderPane(splitPane, opsRegion, null, null, null))

  }

  private def addListeners(): Unit = {
    selectedProperty().addListener(selectedListener)

    splitPane.getDividers.get(0).positionProperty().addListener(dividerPositionListener)
    mutLogFileSettings.autoScrollActiveProperty.addListener(autoScrollListener)
    filtersListProperty.addListener(filterChangeListener)
  }

  private def initBindings(): Unit = {
    filtersListProperty.bind(mutLogFileSettings.filtersProperty)
    //  logVisualView.blockViewPane.blockSizeProperty.bind(opsRegion.opsToolBar.blockSizeProperty)
    textProperty.bind(Bindings.concat(LogFileUtil.logFileName(pathAsString)))
  }

  private def removeListeners(): Unit = {
    selectedProperty().removeListener(selectedListener)

    splitPane.getDividers.get(0).positionProperty().removeListener(dividerPositionListener)

    mutLogFileSettings.autoScrollActiveProperty.removeListener(autoScrollListener)
    filtersListProperty.removeListener(filterChangeListener)
  }

  def shutdown(): Unit = {
    if (mutLogFileSettings.isAutoScrollActive) {
      stopTailer()
    }
    removeListeners()
    LogoRRRGlobals.removeLogFile(pathAsString)
  }

  def setDivider(pos: Double): Unit = {
    splitPane.getDividers.get(0).setPosition(pos)
  }


  def addFilter(filter: Filter): Unit = filtersListProperty.add(filter)

  def removeFilter(filter: Filter): Unit = filtersListProperty.remove(filter)


}


