package net.ladstatt.logboard

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.{Button, ScrollPane, Tab, ToolBar}
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane

object ReportTab {

  def apply(logReport: LogReport): ReportTab = {
    val rt = new ReportTab
    rt.setUserData(logReport)
    rt.setText(logReport.name + " (" + logReport.entries.size() + " entries)")
    rt
  }

}

class ReportTab extends Tab {
  /**
   * initially, report tab will be painted when added. per default the newly added report will be painted once.
   * repaint property will be set to true if the report tab itself is not selected and the width of the application
   * changes. the tab will be repainted when selected.
   **/
  val repaintProperty = new SimpleBooleanProperty(false)

  def setRepaint(repaint: Boolean): Unit = repaintProperty.set(repaint)

  def getRepaint(): Boolean = repaintProperty.get()

  def paint(squareWidth: Int, canvasWidth: Int): Unit = {
    val logReport = getUserData.asInstanceOf[LogReport]
    val image = Painter.paint(logReport.entries, squareWidth, canvasWidth)

    val bp = new BorderPane()
    val bar = new ToolBar()

    LogSeverity.seq.stream.forEach(ls => {
      bar.getItems.add(new Button(ls.name + ": " + logReport.occurences.get(ls) + " " + logReport.percentAsString(ls) + "%"))
    })

    bp.setTop(bar)
    bp.setCenter(new ScrollPane(new ImageView(image)))
    setContent(bp)
  }
}