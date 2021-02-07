package net.ladstatt.logboard.views

import javafx.collections.FXCollections
import javafx.scene.control.{Button, ListView, Tab, ToolBar}
import javafx.scene.layout.BorderPane
import net.ladstatt.logboard.{LogEntry, LogReport, LogSeverity}

class LogTextView(logReport: LogReport) extends Tab("Text View") {

  def doRepaint(sWidth :Int, cWidth :Int) :Unit = ()

  def mkListView(): ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(FXCollections.observableList(logReport.entries))
    lv
  }

  def mkStatsToolbar(): ToolBar = {
    val bar = new ToolBar()
    LogSeverity.seq.stream.forEach(ls => {
      bar.getItems.add(new Button(ls.name + ": " + logReport.occurences.get(ls) + " " + logReport.percentAsString(ls) + "%"))
    })
    bar
  }

  val bp = new BorderPane()
  bp.setTop(mkStatsToolbar())
  bp.setCenter(mkListView())
  setContent(bp)
}
