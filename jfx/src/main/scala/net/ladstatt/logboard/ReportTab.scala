package net.ladstatt.logboard

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