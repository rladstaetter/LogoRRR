package app.logorrr.views.autoscroll

import app.logorrr.model.LogEntry
import app.logorrr.util.CanLog
import javafx.collections.ObservableList
import org.apache.commons.io.input.Tailer

import java.nio.file.Paths

/**
 * If active, this class adds entries to the given logEntries observable list.
 * @param pathAsString path to log file
 * @param logEntries list which will be modified if log file changes
 */
case class LogTailer(pathAsString: String
                     , logEntries: ObservableList[LogEntry])
  extends CanLog {

  var currentTailer: Option[Tailer] = None

  private def mkTailer(): Tailer = new Tailer(Paths.get(pathAsString).toFile, new LogEntryListener(pathAsString, logEntries), 40, true)

  /** start observing log file for changes */
  def start(): Unit = synchronized {
    currentTailer match {
      case Some(value) => logWarn("Not starting new LogTailer, already one in progress ...")
      case None =>
        currentTailer = Option(mkTailer())
        timeR(currentTailer.foreach(t => new Thread(t).start()), s"Started LogTailer for file $pathAsString")
    }
  }

  def stop(): Unit = timeR({
    currentTailer match {
      case Some(tailer) =>
        tailer.stop()
        currentTailer = None
      case None =>
        logWarn("No LogTailer was active, ignoring ...")
    }
  }, s"Stopped LogTailer for file $pathAsString")

}
