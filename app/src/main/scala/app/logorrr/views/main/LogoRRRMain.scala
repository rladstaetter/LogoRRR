package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogFileTab
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import javafx.stage.Window

import java.nio.file.Path
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class LogoRRRMain(closeStage: => Unit)
  extends BorderPane
    with CanLog {
  val mB = new LogoRRRMenuBar(getWindow _, openLogFile, closeAllLogFiles(), closeStage)
  val ambp = new LogoRRRMainBorderPane()

  init()

  def getWindow(): Window = getScene.getWindow()

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)
    val entries = LogoRRRGlobals.getOrderedLogFileSettings
    if (entries.nonEmpty) {
      loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings)
    } else {
      logInfo("No log files loaded.")
    }
    JfxUtils.execOnUiThread(ambp.init())
  }

  private def loadLogFiles(logs: Seq[LogFileSettings]): Unit = {
    Future.sequence {
      logInfo(s"Loading ${logs.length} log files: " + logs.map(_.pathAsString).mkString("['", "',`'", "']"))
      logs.filter(s => !ambp.contains(s.pathAsString)).map(s => Future((s.pathAsString, s.readEntries())))
    }.onComplete({
      case Success(lfs: Seq[(String, ObservableList[LogEntry])]) =>
        lfs.foreach({
          case (pathAsString, es) => JfxUtils.execOnUiThread({
            logTrace(s"Loading `$pathAsString` with ${es.size()} entries.")
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
    })
  }

  /** called when 'Open File' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString
    logTrace(s"Try to open log file $pathAsString")

    if (!ambp.contains(pathAsString)) {
      ambp.addLogFile(path)
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
