package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogFileSettings
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.layout.BorderPane
import javafx.stage.Window

import java.nio.file.Path
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class LogoRRRMain(closeStage: => Unit) extends BorderPane with CanLog {

  val menuBar = new MainMenuBar(() => getWindow, openLogFile, closeAllLogFiles(), closeStage)
  val mainTabPane = new MainTabPane

  init()

  def getLogFileTabs: mutable.Seq[LogFileTab] = mainTabPane.getLogFileTabs

  def getWindow: Window = getScene.getWindow()

  def init(): Unit = {
    setTop(menuBar)
    setCenter(mainTabPane)
    val entries = LogoRRRGlobals.getOrderedLogFileSettings
    if (entries.nonEmpty) {
      loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings)
    } else {
      logInfo("No log files loaded.")
    }
    mainTabPane.init()
  }

  private def loadLogFiles(settings: Seq[LogFileSettings]): Unit = {
    val futures: Future[Seq[LogFileTab]] = Future.sequence {
      val filtered =
        settings.map(lfs => Future {
          val entries = lfs.readEntries()
          val tab = new LogFileTab(LogoRRRGlobals.getLogFileSettings(lfs.pathAsString), entries)
          tab.init()
          logTrace(s"Loaded ${lfs.pathAsString} with ${entries.size()} lines ... ")
          tab
        })
      filtered
    }
    val lfs = Await.result(futures, Duration.Inf)
    logTrace("Loaded " + lfs.size + " files ... ")



    // only after loading all files we initialize the 'add' listener
    // otherwise we would overwrite the active log everytime
    mainTabPane.initSelectionListener()

    JfxUtils.execOnUiThread {
      lfs.foreach(tab => mainTabPane.addLogFileTab(tab))
      // after loading everything, set active log like specified in the config file
      // select log has to be performed on the fx thread
      LogoRRRGlobals.getSomeActive.foreach(pathAsString => {
        selectLog(pathAsString).scrollToActiveElementAndRepaint()
      })
    }
  }

  /** called when 'Open File' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString

    if (!mainTabPane.contains(pathAsString)) {
      mainTabPane.addLogFile(path)
    } else {
      mainTabPane.selectLog(pathAsString).scrollToActiveElementAndRepaint()
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
