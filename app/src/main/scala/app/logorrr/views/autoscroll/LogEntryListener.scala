package app.logorrr.views.autoscroll

import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import javafx.collections.ObservableList
import net.ladstatt.util.log.CanLog
import org.apache.commons.io.input.{Tailer, TailerListener}

/**
 * Inform observable list about events from Tailer.
 *
 * Like append a new element, or react if file was rotated.
 *
 * @param ol list containing current entries
 */
class LogEntryListener(ol: ObservableList[LogEntry]) extends TailerListener with CanLog {

  private var currentCnt = ol.size()

  override def init(tailer: Tailer): Unit = ()

  override def handle(l: String): Unit = {
    currentCnt = currentCnt + 1
    val e = LogEntry(currentCnt, l, None, None)
    JfxUtils.execOnUiThread(ol.add(e))
  }

  override def fileNotFound(): Unit = {
    JfxUtils.execOnUiThread(ol.clear())
  }

  override def fileRotated(): Unit = {
    currentCnt = 0
    JfxUtils.execOnUiThread(ol.clear())
  }

  // ignore exceptions for the moment ...
  override def handle(ex: Exception): Unit = {
    logException("Tailer exception:", ex)
  }
}

