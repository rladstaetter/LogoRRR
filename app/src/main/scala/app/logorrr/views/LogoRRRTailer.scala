package app.logorrr.views

import app.logorrr.model.LogEntry
import app.logorrr.util.LogEntryListener
import javafx.collections.ObservableList
import org.apache.commons.io.input.Tailer

import java.nio.file.Paths

case class LogoRRRTailer(pathAsString: String
                         , logEntries: ObservableList[LogEntry])
  extends Tailer(Paths.get(pathAsString).toFile, new LogEntryListener(pathAsString, logEntries), 1000, true) {

  /** start observing log file for changes */
  def start(): Unit = new Thread(this).start()


}
