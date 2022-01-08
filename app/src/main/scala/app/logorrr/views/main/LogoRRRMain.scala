package app.logorrr.views.main

import app.logorrr.conf.Settings
import app.logorrr.model.LogReportDefinition
import app.logorrr.util.CanLog
import app.logorrr.views.LogColumnDef
import javafx.application.HostServices
import javafx.scene.layout.BorderPane

import java.nio.file.{Path, Paths}

class LogoRRRMain(hostServices: HostServices
                  , closeStage: => Unit
                  , settings: Settings) extends BorderPane
  with CanLog {

  val width = settings.stageSettings.width
  val height = settings.stageSettings.height

  val mB = new LogoRRRMenuBar(openLogFile, removeAllLogFiles(), updateLogReportDefinition, closeStage, hostServices)
  val ambp = AppMainBorderPane(settings, initFileMenu())

  init()

  def updateLogReportDefinition(logFileDef: LogReportDefinition) : Unit = {
    ambp.updateLogFile(logFileDef)
  }

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)

    // reverse since most recently file is saved first in list but should be opened last (= is last log file)
    for (logFileDef <- settings.recentFiles.logReportDefinition.reverse) {
      addLogFile(logFileDef)
    }
    selectLastLogFile()
  }

  /** called when 'Open File' is or an entry of 'Recent Files' is selected. */
  def openLogFile(path: Path): Unit = {
    logTrace(s"Opens log file ${path.toAbsolutePath.toString}")
    ambp.addLogReport(LogReportDefinition(path))
    initFileMenu()
    selectLastLogFile()
  }

  /** removes all log files */
  def removeAllLogFiles(): Unit = {
    shutdown()
    Settings.updateRecentFileSettings(rf => rf.clear())
    initFileMenu()
  }

  private def initFileMenu(): Unit = mB.init()

  def setSceneWidth(sceneWidth: Int): Unit = ambp.setSceneWidth(sceneWidth)

  def addLogFile(lrd: LogReportDefinition): Unit = ambp.addLogReport(lrd)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}
