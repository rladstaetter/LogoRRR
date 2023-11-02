package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.logfiletab.LogFileTab
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import javafx.stage.Window

import java.nio.file.Path
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class LogoRRRMain(closeStage: => Unit)
  extends BorderPane
    with CanLog {

  val menuBar = new LogoRRRMenuBar(() => getWindow, openLogFile, closeAllLogFiles(), closeStage)
  val mainBorderPane = new LogoRRRMainBorderPane()

  init()


  def getLogFileTabs: mutable.Seq[LogFileTab] = mainBorderPane.logViewTabPane.getLogFileTabs

  def getWindow: Window = getScene.getWindow()

  def init(): Unit = {
    setTop(menuBar)
    setCenter(mainBorderPane)
    val entries = LogoRRRGlobals.getOrderedLogFileSettings
    if (entries.nonEmpty) {
      loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings)
    } else {
      logInfo("No log files loaded.")
    }
    JfxUtils.execOnUiThread(mainBorderPane.init())
  }

  private def loadLogFiles(logs: Seq[LogFileSettings]): Unit = {
    val futures: Future[Seq[(LogFileSettings, ObservableList[LogEntry])]] = Future.sequence {
      logInfo(s"Loading ${logs.length} log files: ${logs.map(_.pathAsString).mkString("['", "',`'", "']")}")
      logs.filter(s => !mainBorderPane.contains(s.pathAsString)).map(settings => Future((settings, settings.readEntries())))
    }
    futures.onComplete({
      case Success(lfs: Seq[(LogFileSettings, ObservableList[LogEntry])]) =>
        JfxUtils.execOnUiThread({
          lfs.foreach({
            case (lfs, es) =>
              val tab = new LogFileTab(lfs.pathAsString, es)
              tab.init()
              mainBorderPane.addLogFileTab(tab)
              tab.repaint()
          })

          // only after loading all files we initialize the 'add' listener
          // otherwise we would overwrite the active log everytime
          mainBorderPane.logViewTabPane.initSelectionListener()

          // after loading everything, set active log like specified in the config file
          LogoRRRGlobals.getSomeActive.foreach(p => selectLog(p))

        })
      case Failure(exception) =>
        logException("Could not load logfiles", exception)
        // init listener also in error case
        mainBorderPane.logViewTabPane.initSelectionListener()
    }

    )
  }

  /** called when 'Open File' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString
    logTrace(s"Try to open log file $pathAsString")

    if (!mainBorderPane.contains(pathAsString)) {
      mainBorderPane.addLogFile(path)
    } else {
      logTrace(s"$pathAsString is already opened, selecting tab ...")
      mainBorderPane.selectLog(pathAsString)
    }

  }

  /** removes all log files */
  def closeAllLogFiles(): Unit = {
    shutdown()
    LogoRRRGlobals.clearLogFileSettings()
  }

  def selectLog(path: String): Unit = mainBorderPane.selectLog(path)

  def selectLastLogFile(): Unit = mainBorderPane.selectLastLogFile()

  def shutdown(): Unit = mainBorderPane.shutdown()

}
