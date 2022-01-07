package app.logorrr.util

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import org.apache.commons.io.input.{Tailer, TailerListener}

class LTailerListener(ol: ObservableList[LogEntry]) extends TailerListener with CanLog {

  override def init(tailer: Tailer): Unit = ()

  override def handle(l: String): Unit = {
    JfxUtils.execOnUiThread(ol.add(LogEntry(l)))
  }

  override def fileNotFound(): Unit = {
    JfxUtils.execOnUiThread(ol.clear())
  }

  override def fileRotated(): Unit = {
    JfxUtils.execOnUiThread(ol.clear())
  }

  // ignore exceptions for the moment ...
  override def handle(ex: Exception): Unit = {
    logError(ex.toString)
  }
}

