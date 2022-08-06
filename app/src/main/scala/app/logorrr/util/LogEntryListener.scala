package app.logorrr.util

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.views.search.Filter
import javafx.collections.ObservableList
import org.apache.commons.io.input.{Tailer, TailerListener}

/**
 * Inform observable list about events from Tailer.
 *
 * Like append a new element, or react if file was rotated.
 *
 * @param ol list containing current entries
 */
class LogEntryListener(pathAsString: String
                       , ol: ObservableList[LogEntry])
  extends TailerListener with CanLog {

  var currentCnt = ol.size()

  override def init(tailer: Tailer): Unit = ()

  override def handle(l: String): Unit = {
    currentCnt = currentCnt + 1
    val filters = LogoRRRGlobals.getLogFileSettings(pathAsString).getFilters()
    val e = LogEntry(currentCnt, Filter.calcColor(l, filters), l, None)
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

