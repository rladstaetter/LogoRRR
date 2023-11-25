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
      logInfo(s"Loading ${settings.length} log files: ${settings.map(_.pathAsString).mkString("['", "',`'", "']")}")
      val filtered =
        settings.map(lfs => Future {
          val tab = new LogFileTab(LogoRRRGlobals.getLogFileSettings(lfs.pathAsString), lfs.readEntries())
          tab.init()
          tab
        })
      filtered
    }
    val lfs = Await.result(futures, Duration.Inf)

    println("no jfx thread before this")
    lfs.foreach(tab => mainTabPane.addLogFileTab(tab))

    // only after loading all files we initialize the 'add' listener
    // otherwise we would overwrite the active log everytime
    mainTabPane.initSelectionListener()

    // after loading everything, set active log like specified in the config file
    // select log has to be performed on the fx thread
    LogoRRRGlobals.getSomeActive.foreach(pathAsString => {
      JfxUtils.execOnUiThread({
        val lft = selectLog(pathAsString)
        lft.scrollToActiveChunk()
        lft.repaint()
      })
    })
  }

  /** called when 'Open File' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString

    if (!mainTabPane.contains(pathAsString)) {
      mainTabPane.addLogFile(path)
    } else {
      val lft = mainTabPane.selectLog(pathAsString)
      lft.scrollToActiveChunk()
      lft.repaint()
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
