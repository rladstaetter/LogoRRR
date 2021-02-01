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

  var bp = new BorderPane()
  var bar = new ToolBar()
  var sp = new ScrollPane()
  var iv = new ImageView()

  def cleanup(): Unit = {
    /*
    setUserData(null)
    setText(null)
    iv.setImage(null)
    iv = null
    bar.getItems.clear()
    bp.setTop(null)
    sp.setContent(null)
    bp.setCenter(null)
    sp = null
    bar = null
    bp = null

     */
  }

  def paint(squareWidth: Int, canvasWidth: Int): Unit = {
    val logReport = getUserData.asInstanceOf[LogReport]
    val image = Painter.paint(logReport.entries, squareWidth, canvasWidth)


    val labels =
      LogSeverity.seq.map(ls => {
        val l = new Button(ls.name + ": " + logReport.occurences(ls) + " " + logReport.percentAsString(ls) + "%")
        // l.setPadding(new Insets(0.0D, 5.0D, 0.0D, 0.0D))
        l
      })

    bar.getItems.addAll(labels: _*)
    bp.setTop(bar)
    iv.setImage(image)
    sp.setContent(iv)
    bp.setCenter(sp)
    setContent(bp)
  }
}