package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogFileTab
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane

import java.nio.file.Path
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class LogoRRRMain(closeStage: => Unit)
  extends BorderPane
    with CanLog {

  val mB = new LogoRRRMenuBar(openLogFile, closeAllLogFiles(), closeStage)
  val ambp = new LogoRRRMainBorderPane()

  init()

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)
    loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings())
  }

  private def loadLogFiles(logs: Seq[LogFileSettings]): Unit = {
    Future.sequence(
      logs.filter(s => !ambp.contains(s.pathAsString)).map(s => Future((s.pathAsString, s.readEntries())))
    ).onComplete({
      tryLfs =>
        tryLfs match {
          case Success(lfs: Seq[(String, ObservableList[LogEntry])]) =>
            lfs.foreach({
              case (pathAsString, es) => JfxUtils.execOnUiThread({
                ambp.addLogFileTab(LogFileTab(pathAsString, es))
                LogoRRRGlobals.getSomeActive() match {
                  case Some(value) if pathAsString == value =>
                    selectLog(value)
                  case _ =>
                }
              })
            })
          case Failure(exception) =>
            logException("Could not load logfiles", exception)
        }

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
    LogoRRRGlobals.clearLogFileSettings()
  }

  def selectLog(path: String): Unit = ambp.selectLog(path)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}
