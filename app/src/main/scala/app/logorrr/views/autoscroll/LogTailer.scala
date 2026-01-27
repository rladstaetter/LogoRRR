package app.logorrr.views.autoscroll

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import net.ladstatt.util.log.TinyLog

import java.io.{File, RandomAccessFile}
import java.nio.charset.StandardCharsets
import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, TimeUnit}

/**
 * Monitors the log file for new lines using a scheduled executor and NIO.
 */
case class LogTailer(fileId: FileId, logEntries: ObservableList[LogEntry]) extends TinyLog {

  // Use a dedicated, single-threaded scheduler for all polling operations
  private var someScheduler: Option[ScheduledExecutorService] = None
  private var future: Option[ScheduledFuture[?]] = None
  private var lastPosition: Long = 0L
  private val logFile: File = fileId.asPath.toFile
  private val pollingDelayMs = 500L // Poll every half-second

  // Current line counter for LogEntry ID
  private var currentCnt: Int = logEntries.size()

  /** Reads the file from the last known position to the end. */
  private def readNewLines(): Unit =
    // We synchronize the reading/position update to prevent concurrent issues
    synchronized:
      val currentLength = logFile.length()
      // Handle File Rotation: If the file size has drastically shrunk, clear and reset.
      if currentLength < lastPosition then
        logInfo(s"Log file ${fileId.value} rotated. Clearing entries.")
        currentCnt = 0
        lastPosition = 0L // Reset position to start of the new file
        javafx.application.Platform.runLater(() => logEntries.clear())

      // Read new content only if the file size has increased
      if currentLength > lastPosition then
        var raf: RandomAccessFile = null
        try
          raf = new RandomAccessFile(logFile, "r")
          raf.seek(lastPosition)

          var lineBytes: String = null

          // Use raf.readLine() which reads a line and advances the file pointer.
          while
            lineBytes = raf.readLine()
            lineBytes != null
          do
            // **CRITICAL:** Decode bytes using the correct Charset (e.g., UTF-8)
            // Note: readLine() returns ISO-8859-1 string, which we must correctly re-encode.
            val line = new String(lineBytes.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)

            processLine(line)

          // Update the position for the next poll
          lastPosition = raf.getFilePointer

        catch
          case e: Exception => logException(s"Error while reading new lines from ${fileId.value}", e)
        finally
          if raf != null then raf.close()

  private def processLine(line: String): Unit =
    currentCnt = currentCnt + 1
    val e = LogEntry(currentCnt, line, None, None)
    javafx.application.Platform.runLater(() => {
      logEntries.add(e)
    })

  /** Start observing log file for changes */
  def start(): Unit = synchronized:
    future match
      case Some(_) => logWarn("Not starting new LogTailer, already one in progress ...")
      case None =>
        // 1. Set initial position to the end of the file (TailFromEnd behavior)
        lastPosition = logFile.length()

        val r = new Runnable:
          override def run(): Unit = readNewLines()
        // 2. Schedule the polling task
        //val runnable: Runnable = () => readNewLines()
        someScheduler = Option(Executors.newSingleThreadScheduledExecutor())
        future = someScheduler.map(_.scheduleWithFixedDelay(r,
          0, // Initial delay (start reading immediately)
          pollingDelayMs,
          TimeUnit.MILLISECONDS
        ))
        logInfo(s"Started LogTailer for file ${fileId.value} with ${pollingDelayMs}ms delay.")

  /** Stop observing log file */
  def stop(): Unit = {
    if (future.isEmpty && someScheduler.isEmpty) {
      logTrace(s"LogTailer inactive for ${fileId.asPath}.")
    } else
      timeR({
        future.foreach(_.cancel(true))
        someScheduler.foreach(_.shutdown())
        someScheduler = None
        future = None
      }, s"Stopped LogTailer for file ${fileId.asPath}")
  }

}