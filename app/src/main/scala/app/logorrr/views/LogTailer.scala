package app.logorrr.views

import org.apache.commons.io.input.Tailer

trait LogTailer {

  val tailer: Tailer

  /** start observing log file for changes */
  def startTailer(): Unit = new Thread(tailer).start()

  /** stop observing changes */
  def stopTailer(): Unit = tailer.stop()

}
