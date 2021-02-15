package net.ladstatt.logboard.views

import javafx.beans.{InvalidationListener, Observable}
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import net.ladstatt.logboard.{LogEntry, LogReport}
import net.ladstatt.util.CanLog


class LogView(logReport: LogReport
              , squareWidth: Int
              , initialCanvasWith: Int) extends Tab(logReport.name + " (" + logReport.entries.size() + " entries)") with CanLog {

  /** top component for log view */
  val borderPane = new BorderPane()

  val splitPane = new SplitPane()

  // defaults to view all entries
  val filteredList = new FilteredList[LogEntry](FXCollections.observableList(logReport.entries))

  // to detect when we apply a new filter via filter buttons (see FilterButtonsToolbar)
  filteredList.predicateProperty().addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      val width = splitPane.getDividers.get(0).getPosition * splitPane.getWidth
      if (isSelected && width > squareWidth) {
        logVisualView.doRepaint(squareWidth, width.toInt)
      }
    }
  })

  private val filterButtonsToolBar = new FilterButtonsToolBar(filteredList, logReport.occurences, logReport.entries.size)

  private val logVisualView = new LogVisualView(filteredList, squareWidth, (initialCanvasWith * 0.5).toInt)
  private val logTextView = new LogTextView(filteredList)

  borderPane.setTop(filterButtonsToolBar)
  borderPane.setCenter(splitPane)

  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   * */
  val repaintProperty = new SimpleBooleanProperty(false)

  val selectedIndexProperty = new SimpleIntegerProperty()

  selectedIndexProperty.bind(logVisualView.selectedIndexProperty)
  selectedIndexProperty.addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      logTextView.selectEntryByIndex(t1.intValue())
    }
  })

  splitPane.getItems.addAll(logVisualView, logTextView)

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

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()

  def doRepaint(cWidth: Int): Unit = {
    logVisualView.doRepaint(squareWidth, cWidth)
  }
}