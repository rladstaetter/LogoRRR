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

  def getWindow: Window = getScene.getWindow()

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
    val futures: Future[Seq[(String, ObservableList[LogEntry])]] = Future.sequence {
      logInfo(s"Loading ${logs.length} log files: ${logs.map(_.pathAsString).mkString("['", "',`'", "']")}")
      logs.filter(s => !ambp.contains(s.pathAsString)).map(s => Future((s.pathAsString, s.readEntries())))
    }
    futures.onComplete({
      case Success(lfs: Seq[(String, ObservableList[LogEntry])]) =>
        JfxUtils.execOnUiThread({
          lfs.foreach({
            case (pathAsString, es) =>
              logTrace(s"Loading `$pathAsString` with ${es.size()} entries.")
              ambp.addLogFileTab(LogFileTab(pathAsString, es))
          })
          // after loading everything, set active log like specified in the config file
          LogoRRRGlobals.getSomeActive match {
            case Some(selectedPath) =>
              logTrace(s"Active Path: $selectedPath")
              lfs.map(_._1).zipWithIndex.find(tpl => tpl._1 == selectedPath) match {
                case Some((path, index)) =>
                  selectLog(path)
                case None =>
                  logWarn("not found removing active entry")
                  LogoRRRGlobals.setSomeActive(None)
              }
            case None => logTrace("No active path entry found")
          }
          // only after loading all files we initialize the 'add' listener
          // otherwise we would overwrite the active log everytime
          ambp.logViewTabPane.initLogFileAddListener()
        })
      case Failure(exception) =>
        logException("Could not load logfiles", exception)
        // init listener also in error case
        ambp.logViewTabPane.initLogFileAddListener()
    }

    )
  }

  /** called when 'Open File' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString
    logTrace(s"Try to open log file $pathAsString")

    if (!ambp.contains(pathAsString)) {
      ambp.addLogFile(path)
    } else {
      logTrace(s"$pathAsString is already opened, selecting tab ...")
      ambp.selectLog(pathAsString)
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
