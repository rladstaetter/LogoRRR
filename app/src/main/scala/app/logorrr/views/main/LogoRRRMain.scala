package app.logorrr.views.main

import app.logorrr.conf.{LogoRRRGlobals, Settings}
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogFileTab
import javafx.collections.FXCollections
import javafx.scene.layout.BorderPane

import java.nio.file.Path
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LogoRRRMain(closeStage: => Unit)
  extends BorderPane
    with CanLog {

  val mB = new LogoRRRMenuBar(openLogFile, closeStage)
  val ambp = new LogoRRRMainBorderPane()

  init()

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)

    Future.sequence(LogoRRRGlobals.allLogs().map(s => Future({
      if (!ambp.contains(s.pathAsString)) {
        (s.readEntries(), s.pathAsString)
      } else {
        logWarn(s"Path ${s.pathAsString} is already opened ...")
        (FXCollections.observableArrayList[LogEntry](), s.pathAsString)
      }
    }).map {
      case ((entries, path)) if (!entries.isEmpty) =>
        JfxUtils.execOnUiThread(ambp.addLogFileTab(LogFileTab(path, entries)))
      case (_, path) => logWarn(s"Could not read $path. No entries found.")
    })).onComplete({
      _ =>
        LogoRRRGlobals.getSomeActive() match {
          case Some(value) => selectLog(value)
          case None =>
            logError("No active log file entries found.")
            selectLastLogFile()
        }
        // only after having initialized we activate change listeners */
        JfxUtils.execOnUiThread(ambp.init())
    })

  }

  /** called when 'Open File' is or an entry of 'Recent Files' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString
    logTrace(s"Try to open log file $pathAsString")

    if (!ambp.contains(pathAsString)) {
      val logFileSettings = LogFileSettings(path)
      LogoRRRGlobals.updateLogFile(logFileSettings)
      ambp.addLogFileTab(LogFileTab(pathAsString, logFileSettings.readEntries()))
      selectLog(pathAsString)
    } else {
      logTrace("File is already opened.")
    }

  }

  /** removes all log files */
  def closeAllLogFiles(): Unit = {
    shutdown()
    LogoRRRGlobals.resetLogs()
  }

  def selectLog(path: String): Unit = ambp.selectLog(path)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}
