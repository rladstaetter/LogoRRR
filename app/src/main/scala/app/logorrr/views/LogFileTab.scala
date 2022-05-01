package app.logorrr.views

import app.logorrr.conf.{BlockSettings, LogoRRRGlobals, Settings, SettingsIO}
import app.logorrr.model.{LogEntry, LogEntryFileReader, LogFileSettings}
import app.logorrr.util._
import app.logorrr.views.visual.LogVisualView
import javafx.application.HostServices
import javafx.beans.binding.{Bindings, StringExpression}
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.value.ChangeListener
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{ListChangeListener, ObservableList}
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.layout._
import org.apache.commons.io.input.Tailer

import java.nio.file.Paths
import java.time.Instant
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

object LogFileTab {

  case class TimeRange(startTime: Instant, endTime: Instant)

  def apply(pathAsString: String, logEntries: ObservableList[LogEntry], initFileMenu: => Unit): LogFileTab = {
    val logFileTab = new LogFileTab(pathAsString, logEntries, initFileMenu)


    /** activate invalidation listener on filtered list */
    logFileTab.init()
    logFileTab
  }

}

trait LogTailer {

  val tailer: Tailer

  /** start observing log file for changes */
  def startTailer(): Unit = new Thread(tailer).start()

  /** stop observing changes */
  def stopTailer(): Unit = tailer.stop()

}

/**
 * Represents a single 'document' UI approach for a log file.
 *
 * One can view / interact with more than one log file at a time, using tabs here feels quite natural.
 *
 * @param logEntries report instance holding information of log file to be analyzed
 * */
class LogFileTab(val pathAsString: String
                 , val logEntries: ObservableList[LogEntry]
                 , initFileMenu: => Unit)
  extends Tab
    with LogTailer
    with CanLog {

  def repaint() = logVisualView.repaint()

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

  val tailer = new Tailer(Paths.get(pathAsString).toFile, new LogEntryListener(logEntries), 1000, true)

  /** list of search filters to be applied */
  val filtersListProperty = new SimpleListProperty[Filter](CollectionUtils.mkEmptyObservableList())


  /** split visual view and text view */
  val splitPane = new SplitPane()

  /** list which holds all entries, default to display all (can be changed via buttons) */
  val filteredList = new FilteredList[LogEntry](logEntries)

  private val searchToolBar = new SearchToolBar(addFilter)

  private val filtersToolBar = {
    val fbtb = new FiltersToolBar(filteredList, logEntries.size, removeFilter)
    fbtb.filtersProperty.bind(filtersListProperty)
    fbtb
  }

  val settingsToolBar = new SettingsToolBar(pathAsString)

  val opsBorderPane: OpsBorderPane = {
    val op = new OpsBorderPane(searchToolBar, filtersToolBar, settingsToolBar)
    op.blockSizeProperty.set(LogoRRRGlobals.getLogFileSettings(pathAsString).blockWidthSettingsProperty.get())
    op
  }

  val initialWidth = (Bindings.multiply(LogoRRRGlobals.settings.stageSettings.widthProperty, LogoRRRGlobals.getLogFileSettings(pathAsString).dividerPositionProperty))
  //val initialWidth = (sceneWidth * initialLogFileSettings.dividerPosition).toInt

  private lazy val logVisualView = {
    val lvv = new LogVisualView(filteredList, initialWidth.intValue())
    lvv.blockViewPane.visibleProperty().bind(selectedProperty())
    //    lvv.blockViewPane.blockSizeProperty.set(initialLogFileSettings.blockSettings.width)
    //       lvv.sisp.filtersListProperty.bind(filtersListProperty)
    lvv
  }

  lazy val timings: Map[Int, Instant] =
    logEntries.stream().collect(Collectors.toMap((le: LogEntry) => le.lineNumber, (le: LogEntry) => le.someInstant.getOrElse(Instant.now()))).asScala.toMap

  private val logTextView = new LogTextView(filteredList, timings)

  val entryLabel = {
    val l = new Label("")
    l.prefWidthProperty.bind(LogoRRRGlobals.settings.stageSettings.widthProperty)
    l.setStyle(LogoRRRFonts.jetBrainsMono(20))
    l
  }


  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  private val logEntryChangeListener: ChangeListener[LogEntry] = JfxUtils.onNew[LogEntry](updateEntryLabel)


  /** if a change event for filtersList Property occurs, save it to disc */
  def initFiltersPropertyListChangeListener(): Unit = {
    filtersListProperty.addListener(JfxUtils.mkListChangeListener(handleFilterChange))
  }

  /** update all log entries with current filter settings */
  def updateLogEntryColors(): Unit = {
    val filters = filtersListProperty.get().asScala.toSeq
    val lE: mutable.Seq[LogEntry] = for (old <- logEntries.asScala) yield old.copy(color = Filter.calcColor(old.value, filters))
    logEntries.setAll(lE.asJava)
  }

  private def handleFilterChange(change: ListChangeListener.Change[_ <: Fltr]): Unit = {
    while (change.next()) {
      Future {
        LogoRRRGlobals.persist()
      }
      updateLogEntryColors()
    }
  }

  def init(): Unit = {
    filtersListProperty.bind(LogoRRRGlobals.getLogFileSettings(pathAsString).filtersProperty)

    /** top component for log view */
    val borderPane = new BorderPane()
    borderPane.setTop(opsBorderPane)
    borderPane.setCenter(splitPane)
    borderPane.setBottom(entryLabel)
    setContent(borderPane)

    logVisualView.blockViewPane.blockSizeProperty.bind(opsBorderPane.blockSizeProperty)
    logVisualView.blockViewPane.blockSizeProperty.addListener(JfxUtils.onNew[Number](n => {
      LogoRRRGlobals.updateBlockSettings(pathAsString, BlockSettings(n.intValue()))
    }))

    /* change active text field depending on visible tab */
    selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean](b => {
      if (b) {
        LogoRRRAccelerators.setActiveSearchTextField(searchToolBar.searchTextField)
        JfxUtils.execOnUiThread(repaint())
      } else {
      }
    }))

    /** don't monitor file anymore if tab is closed, free invalidation listeners */
    setOnClosed(_ => closeTab())
    textProperty.bind(computeTabTitle)

    selectedEntryProperty.bind(logVisualView.selectedEntryProperty)
    selectedEntryProperty.addListener(logEntryChangeListener)

    // if user changes selected item in listview, change footer as well
    logTextView.listView.getSelectionModel.selectedItemProperty.addListener(logEntryChangeListener)

    splitPane.getItems.addAll(logVisualView, logTextView)


    /**
     * we are interested just in the first divider. If it changes its position (which means the user interacts) then
     * update logVisualView
     * */
    splitPane.getDividers.get(0).positionProperty().addListener(JfxUtils.onNew {
      t1: Number => LogoRRRGlobals.updateDividerPosition(pathAsString, t1.doubleValue())
    })

    startTailer()

    setDivider(LogoRRRGlobals.getLogFileSettings(pathAsString).dividerPositionProperty.get())
    initFiltersPropertyListChangeListener()
  }

  /** compute title of tab */
  private def computeTabTitle: StringExpression = {
    Bindings.concat(pathAsString, " (", Bindings.size(logEntries).asString, " lines)")
  }

  /**
   * Actions to perform if tab is closed:
   *
   * - end monitoring of file
   * - update config file
   * - update file menu
   *
   */
  def closeTab(): Unit = {
    initFileMenu
    shutdown()
  }

  def shutdown(): Unit = {
    LogoRRRGlobals.removeLogFile(pathAsString)
    stopTailer()
  }

  def updateEntryLabel(logEntry: LogEntry): Unit = {
    Option(logEntry) match {
      case Some(entry) =>
        val background: Background = entry.background(filtersToolBar.filterButtons.keys.toSeq)
        entryLabel.setBackground(background)
        entryLabel.setTextFill(Filter.calcColor(entry.value, filtersToolBar.filterButtons.keys.toSeq).invert())
        entryLabel.setText(entry.value)
        logTextView.select(logEntry)
      case None =>
        entryLabel.setBackground(null)
        entryLabel.setText("")
    }
  }

  def setDivider(pos: Double): Unit = splitPane.getDividers.get(0).setPosition(pos)

  def addFilter(filter: Filter): Unit = filtersListProperty.add(filter)

  def removeFilter(filter: Filter): Unit = filtersListProperty.remove(filter)


  def getVisualViewWidth(): Double = {
    val w = splitPane.getDividers.get(0).getPosition * splitPane.getWidth
    if (w != 0.0) {
      w
    } else {
      initialWidth.doubleValue()
    }
  }


}