package app.logorrr.views.main

import app.logorrr.conf.{Settings, SettingsIO}
import app.logorrr.model.LogReportDefinition
import app.logorrr.util.CanLog
import app.logorrr.views.Filter
import javafx.application.HostServices
import javafx.scene.layout.BorderPane

import java.nio.file.Path

class LogoRRRMain(hostServices: HostServices
                  , closeStage: => Unit
                  , settings: Settings) extends BorderPane
  with CanLog {

  val width = settings.stageSettings.width
  val height = settings.stageSettings.height

  val mB = new LogoRRRMenuBar(openLogFile, closeAllLogReports, updateLogReportDefinition, closeStage, hostServices)
  val ambp = AppMainBorderPane(settings, initFileMenu)

  init()

  def updateLogReportDefinition(logFileDef: LogReportDefinition): Unit = {
    ambp.updateLogFile(logFileDef)
  }

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)

    // reverse since most recently file is saved first in list but should be opened last (= is last log file)
    for (logFileDef <- settings.recentFiles.logReportDefinition.reverse) {
      addLogReport(logFileDef)
    }
    selectLastLogReport()
  }

  /** called when 'Open File' is or an entry of 'Recent Files' is selected. */
  def openLogFile(path: Path): Unit = {
    logTrace(s"Try to open log file ${path.toAbsolutePath.toString}")

    if (!ambp.contains(path)) {
      SettingsIO.updateRecentFileSettings(rf => rf.copy(logReportDefinition = LogReportDefinition(path.toString, None, Filter.seq) +: rf.logReportDefinition))
      addLogReport(LogReportDefinition(path))
      initFileMenu()
      selectLastLogReport()
    } else {
      logTrace("File is already opened.")
    }

  }

  /** removes all log files */
  def closeAllLogReports(): Unit = {
    shutdown()
    SettingsIO.updateRecentFileSettings(rf => rf.clear())
    initFileMenu()
  }

  private def initFileMenu(): Unit = {
    mB.init()
  }

  def setSceneWidth(sceneWidth: Int): Unit = ambp.setSceneWidth(sceneWidth)

  def addLogReport(lrd: LogReportDefinition): Unit = {
    if (!ambp.contains(lrd.path)) {
      ambp.addLogReport(lrd)
    } else {
      logWarn(s"Path ${lrd.path.toAbsolutePath} is already opened ...")
    }
  }

  def selectLastLogReport(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}
