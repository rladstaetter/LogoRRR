package net.ladstatt.logboard.views

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control._
import net.ladstatt.logboard.LogReport
import net.ladstatt.util.CanLog


object LogView {

  def apply(logReport: LogReport, squareWidth: Int, canvasWidth: Int): LogView = {
    val rt = new LogView(logReport, squareWidth, canvasWidth)
    rt
  }

}




class LogView(logReport: LogReport
              , squareWidth: Int
              , canvasWidth: Int) extends Tab(logReport.name + " (" + logReport.entries.size() + " entries)") with CanLog {

  private val tabPane = new TabPane()
  private val logVisualView = new LogVisualView(logReport, squareWidth, canvasWidth)
  private val logTextView = new LogTextView(logReport)
  tabPane.getTabs.addAll(logVisualView, logTextView)
  setContent(tabPane)

  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   * */
  val repaintProperty = new SimpleBooleanProperty(false)

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()


  def doRepaint(sWidth: Int, cWidth: Int): Unit = {
    logTextView.doRepaint(sWidth, cWidth)
    logVisualView.doRepaint(sWidth, cWidth)
  }
}