package app.logorrr.views

import app.logorrr.conf.{BlockSettings, LogoRRRGlobals}
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util._
import app.logorrr.views.autoscroll.LogTailer
import app.logorrr.views.ops.OpsRegion
import app.logorrr.views.search.{Filter, FiltersToolBar, Fltr, OpsToolBar}
import app.logorrr.views.text.LogTextView
import app.logorrr.views.visual.LogVisualView
import javafx.beans.binding.{Bindings, StringExpression}
import javafx.beans.property.SimpleListProperty
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.transformation.FilteredList
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.scene.control._
import javafx.scene.layout._

import java.time.Instant
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters._


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

  case class TimeRange(startTime: Instant, endTime: Instant)

  def apply(logFileSettings: LogFileSettings): LogFileTab = {
    apply(logFileSettings.pathAsString, logFileSettings.readEntries())
  }

  def apply(pathAsString: String, logEntries: ObservableList[LogEntry]): LogFileTab = {
    val logFileTab = new LogFileTab(pathAsString, logEntries)

    /** activate invalidation listener on filtered list */
    logFileTab.init()
    logFileTab
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
  extends Tab with CanLog {

  // lazy since only if autoscroll is set start tailer
  lazy val logTailer = LogTailer(pathAsString, logEntries)

  private def getLogFileSettings = LogoRRRGlobals.getLogFileSettings(pathAsString)

  /*
    getLogFileSettings.hasLogEntrySettingBinding.addListener(JfxUtils.onNew[java.lang.Boolean](b => {
      if (b) {
        for (i <- 1 to logEntries.size()) {
          val old = logEntries.get(i)
          logEntries.set(i, old.copy(someInstant = LogEntryInstantFormat.parseInstant(old.value, getLogFileSettings.getSomeLogEntrySetting.orNull)))
        }
      }
    }))
  */
  def repaint(): Unit = logVisualView.repaint()

  /** contains time information for first and last entry for given log file */
  lazy val someTimeRange: Option[LogFileTab.TimeRange] =
    if (logEntries.isEmpty) {
      None
    } else {
      val filteredEntries = logEntries.filtered((t: LogEntry) => t.someInstant.isDefined)
      if (filteredEntries.isEmpty) {
        None
      } else {
        for {start <- filteredEntries.get(0).someInstant
             end <- filteredEntries.get(filteredEntries.size() - 1).someInstant} yield LogFileTab.TimeRange(start, end)
      }
    }


  /** list of search filters to be applied */
  val filtersListProperty = new SimpleListProperty[Filter](CollectionUtils.mkEmptyObservableList())

  /** split visual view and text view */
  val splitPane = new SplitPane()

  /** list which holds all entries, default to display all (can be changed via buttons) */
  val filteredList = new FilteredList[LogEntry](logEntries)

  private val opsToolBar = {
    val op = new OpsToolBar(pathAsString, addFilter, logEntries)
    op.blockSizeProperty.set(getLogFileSettings.blockWidthSettingsProperty.get())
    op
  }

  val filtersToolBar = {
    val fbtb = new FiltersToolBar(filteredList, removeFilter)
    fbtb.filtersProperty.bind(filtersListProperty)
    fbtb
  }

  val opsRegion: OpsRegion = {
    val op = new OpsRegion(opsToolBar, filtersToolBar)
    op
  }

  private lazy val logVisualView = {
    val lvv = new LogVisualView(pathAsString, filteredList, LogoRRRGlobals.logVisualCanvasWidth(pathAsString))
    lvv.blockViewPane.visibleProperty().bind(selectedProperty())
    lvv
  }

  private val logTextView = new LogTextView(pathAsString, filteredList)


  lazy val scrollToEndEventListener: InvalidationListener = (_: Observable) => {
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

  def initAutoScroll(): Unit = {
    getLogFileSettings.autoScrollProperty.addListener(JfxUtils.onNew[java.lang.Boolean] {
      b =>
        if (b) {
          startTailer()
        } else {
          stopTailer()
        }
    })

    if (getLogFileSettings.isAutoScroll) {
      startTailer()
    }

  }


  /** if a change event for filtersList Property occurs, save it to disc */
  def initFiltersPropertyListChangeListener(): Unit = {
    filtersListProperty.addListener(JfxUtils.mkListChangeListener(handleFilterChange))
  }

  private def handleFilterChange(change: ListChangeListener.Change[_ <: Fltr]): Unit = {
    while (change.next()) {
      Future {
        LogoRRRGlobals.persist()
      }
    }
  }

  def init(): Unit = {
    filtersListProperty.bind(getLogFileSettings.filtersProperty)

    /** top component for log view */
    val borderPane = new BorderPane()
    borderPane.setTop(opsRegion)
    borderPane.setCenter(splitPane)
    setContent(borderPane)

    logVisualView.blockViewPane.blockSizeProperty.bind(opsRegion.opsToolBar.blockSizeProperty)
    logVisualView.blockViewPane.blockSizeProperty.addListener(JfxUtils.onNew[Number](n => {
      LogoRRRGlobals.setBlockSettings(pathAsString, BlockSettings(n.intValue()))
    }))

    /* change active text field depending on visible tab */
    selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean](b => {
      if (b) {
        LogoRRRAccelerators.setActiveSearchTextField(opsToolBar.searchTextField)
        LogoRRRAccelerators.setActiveRegexToggleButton(opsToolBar.regexToggleButton)
        JfxUtils.execOnUiThread(repaint())
      } else {
      }
    }))
    selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean](b => {
      if (b) {
        setStyle(LogFileTab.BackgroundSelectedStyle)
      } else {
        setStyle(LogFileTab.BackgroundStyle)
      }
    }))


    /** don't monitor file anymore if tab is closed, free invalidation listeners */
    setOnClosed(_ => closeTab())
    textProperty.bind(computeTabTitle)
    setTooltip(mkTabToolTip)

    // selectedEntryProperty.bind(logVisualView.selectedEntryProperty)

    // if user changes selected item in listview, change footer as well
    //logTextView.listView.getSelectionModel.selectedItemProperty.addListener(logEntryChangeListener)

    splitPane.getItems.addAll(logVisualView, logTextView)


    /**
     * we are interested just in the first divider. If it changes its position (which means the user interacts) then
     * update logVisualView
     * */
    splitPane.getDividers.get(0).positionProperty().addListener(JfxUtils.onNew {
      t1: Number => LogoRRRGlobals.setDividerPosition(pathAsString, t1.doubleValue())
    })

    initAutoScroll()

    setDivider(getLogFileSettings.getDividerPosition())
    initFiltersPropertyListChangeListener()
  }

  /** compute tab tooltip */
  private def mkTabToolTip: Tooltip = {
    val tooltip = new Tooltip()
    tooltip.textProperty().bind(Bindings.concat(
      pathAsString, "\n",
      Bindings.size(logEntries).asString, " lines")
    )
    tooltip
  }

  /** compute title of tab */
  private def computeTabTitle: StringExpression = Bindings.concat(LogFileUtil.logFileName(pathAsString))

  /**
   * Actions to perform if tab is closed:
   *
   * - end monitoring of file
   * - update config file
   * - update file menu
   *
   */
  def closeTab(): Unit = shutdown()


  def shutdown(): Unit = {
    if (getLogFileSettings.isAutoScroll) {
      stopTailer()
    }
    LogoRRRGlobals.removeLogFile(pathAsString)
  }


  def setDivider(pos: Double): Unit = splitPane.getDividers.get(0).setPosition(pos)

  def addFilter(filter: Filter): Unit = filtersListProperty.add(filter)

  def removeFilter(filter: Filter): Unit = filtersListProperty.remove(filter)


}