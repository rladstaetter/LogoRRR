package net.ladstatt.logboard.views

import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control._
import net.ladstatt.logboard.LogReport
import net.ladstatt.util.CanLog


class LogView(logReport: LogReport
              , squareWidth: Int
              , initialCanvasWith: Int) extends Tab(logReport.name + " (" + logReport.entries.size() + " entries)") with CanLog {


  val splitPane = new SplitPane()
  private val logVisualView = new LogVisualView(logReport, squareWidth, (initialCanvasWith * 0.5).toInt)
  private val logTextView = new LogTextView(logReport)

  val selectedIndexProperty = new SimpleIntegerProperty()
  selectedIndexProperty.bind(logVisualView.selectedIndexProperty)
  selectedIndexProperty.addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      logTextView.selectEntryByIndex(t1.intValue())
    }
  })

  splitPane.getItems.addAll(logVisualView, logTextView)
  setContent(splitPane)

  splitPane.getDividers.get(0).positionProperty().addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      val width = t1.doubleValue() * splitPane.getWidth
      // t1 can be negative as well
      if (isSelected && width > squareWidth) {
        logVisualView.doRepaint(squareWidth, width.toInt)
      }
    }
  })

  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   * */
  val repaintProperty = new SimpleBooleanProperty(false)

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()

  def doRepaint(cWidth: Int): Unit = {
    logVisualView.doRepaint(squareWidth, cWidth)
  }
}