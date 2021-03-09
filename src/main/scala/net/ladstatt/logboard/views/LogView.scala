package net.ladstatt.logboard.views

import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import net.ladstatt.logboard.{LogEntry, LogReport, LogSeverity, LogViewTabPane}
import net.ladstatt.util.CanLog

import scala.jdk.CollectionConverters._

object LogView {

  def apply(logViewTabPane: LogViewTabPane
            , logReport: LogReport): LogView = {
    val lv = new LogView(logReport
      , logViewTabPane.sceneWidthProperty.get()
      , logViewTabPane.squareWidthProperty.get())
    lv.sceneWidthProperty.bind(logViewTabPane.sceneWidthProperty)
    lv.squareWidthProperty.bind(logViewTabPane.squareWidthProperty)
    lv
  }

}

/**
 * Represents a single 'document' UI approach for a log file.
 *
 * One can view / interact with more than one log file at a time, using tabs here feels quite natural.
 *
 * @param logReport report instance holding information of log file to be analyzed
 * */
class LogView(logReport: LogReport
              , initialSceneWidth: Int
              , initialSquareWidth: Int)
  extends Tab(logReport.name + " (" + logReport.entries.length + " entries)") with CanLog {


  /** bound to sceneWidthProperty of parent LogViewTabPane */
  val sceneWidthProperty = new SimpleIntegerProperty(initialSceneWidth)

  def sceneWidth = sceneWidthProperty.get()

  /** bound to squareWidthProperty of parent LogViewTabPane */
  val squareWidthProperty = new SimpleIntegerProperty(initialSquareWidth)

  def squareWidth = squareWidthProperty.get()

  val InitialRatio = 0.5

  /** top component for log view */
  val borderPane = new BorderPane()

  /** split visual view and text view */
  val splitPane = new SplitPane()

  /** list which holds all entries, default to display all (can be changed via buttons) */
  val filteredList = new FilteredList[LogEntry](FXCollections.observableList(logReport.entries.asJava))

  // to detect when we apply a new filter via filter buttons (see FilterButtonsToolbar)
  filteredList.predicateProperty().addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      doRepaint()
    }
  })


  private val filterButtonsToolBar = new FilterButtonsToolBar(filteredList, logReport.occurrences, logReport.entries.size)

  private lazy val logVisualView = {
    val lvv = new LogVisualView(filteredList.asScala, (sceneWidth * InitialRatio).toInt, squareWidth)
    lvv.doRepaint(squareWidth, (sceneWidth * InitialRatio).toInt)
    lvv
  }
  private val logTextView = new LogTextView(filteredList)

  val entryLabel = {
    val l = new Label("")
    l.prefWidthProperty.bind(sceneWidthProperty)
    l.setStyle(s"-fx-text-fill: white; -fx-font-size: 20px;")
    l
  }

  borderPane.setTop(filterButtonsToolBar)
  borderPane.setCenter(splitPane)
  borderPane.setBottom(entryLabel)

  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   * */
  val repaintProperty = new SimpleBooleanProperty(false)

  /** to share state between visual view and text view. index can be selected by navigation in visual view */
  val selectedIndexProperty = new SimpleIntegerProperty()


  selectedIndexProperty.bind(logVisualView.selectedIndexProperty)
  selectedIndexProperty.addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      logTextView.selectEntryByIndex(t1.intValue())
    }
  })


  val selectedEntryProperty = new SimpleObjectProperty[LogEntry]()

  selectedEntryProperty.bindBidirectional(logVisualView.selectedEntryProperty)

  selectedEntryProperty.addListener(new ChangeListener[LogEntry] {
    override def changed(observableValue: ObservableValue[_ <: LogEntry], t: LogEntry, entry: LogEntry): Unit = {
      entryLabel.setBackground(LogSeverity.backgrounds(entry.severity))
      entryLabel.setText(s"L: ${getSelectedIndex() + 1} ${entry.value}")
    }
  })

  splitPane.getItems.addAll(logVisualView, logTextView)

  /** we are interested just in the first divider */
  splitPane.getDividers.get(0).positionProperty().addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      val width = t1.doubleValue() * splitPane.getWidth
      // t1 can be negative as well
      if (isSelected && width > squareWidth) {
        logVisualView.doRepaint(squareWidth, width.toInt)
      }
    }
  })


  setContent(borderPane)

  def getVisualViewWidth(): Double = splitPane.getDividers.get(0).getPosition * splitPane.getWidth

  def getSelectedIndex(): Int = selectedIndexProperty.get()

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()

  def doRepaint(): Unit = doRepaint(getVisualViewWidth())

  /** width can be negative as well, we have to guard about that. also we repaint only if view is visible. */
  private def doRepaint(width: Double): Unit = timeR({
    if (isSelected && width > squareWidth * 4) { // at minimum we want to have 4 squares left (an arbitrary choice)
      logVisualView.doRepaint(squareWidth, width.toInt)
    }
  }, "repaint")

}