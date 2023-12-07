package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogFileSettings
import app.logorrr.util.CanLog
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.layout.BorderPane
import javafx.stage.Window

import java.nio.file.Path
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class LogoRRRMain(closeStage: => Unit) extends BorderPane with CanLog {

  val mainTabPane = new MainTabPane


  def getLogFileTabs: mutable.Seq[LogFileTab] = mainTabPane.getLogFileTabs

  def getWindow: Window = getScene.getWindow()

  def init(): Unit = {
    setTop(new MainMenuBar(() => getWindow, openLogFile, closeAllLogFiles(), closeStage))
    setCenter(mainTabPane)
    val entries = LogoRRRGlobals.getOrderedLogFileSettings
    if (entries.nonEmpty) {
      loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings)
    } else {
      logTrace("No log files loaded.")
    }
    mainTabPane.init()
  }

  private def loadLogFiles(settings: Seq[LogFileSettings]): Unit = {
    val futures: Future[Seq[LogFileTab]] = Future.sequence {
      settings.map(lfs => Future {
        timeR({
          val entries = lfs.readEntries()
          val tab = new LogFileTab(LogoRRRGlobals.getLogFileSettings(lfs.pathAsString), entries)
          mainTabPane.addLogFileTab(tab)
          tab
        }, s"Loaded '${lfs.pathAsString}'")
      })
    }
    val logFileTabs: Seq[LogFileTab] = Await.result(futures, Duration.Inf)
    logTrace("Loaded " + logFileTabs.size + " files ... ")


    // only after loading all files we initialize the 'add' listener
    // otherwise we would overwrite the active log everytime
    mainTabPane.initSelectionListener()

  }

  /** called when 'Open File' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString

    if (!mainTabPane.contains(pathAsString)) {
      mainTabPane.addLogFile(path)
    } else {
      mainTabPane.selectLog(pathAsString).recalculateChunkListViewAndScrollToActiveElement()
    }

  }

  /** removes all log files */
  def closeAllLogFiles(): Unit = {
    shutdown()
    LogoRRRGlobals.clearLogFileSettings()
  }

  def selectLog(pathAsString: String): LogFileTab = mainTabPane.selectLog(pathAsString)

  def selectLastLogFile(): Unit = mainTabPane.selectLastLogFile()

  def shutdown(): Unit = mainTabPane.shutdown()

}
