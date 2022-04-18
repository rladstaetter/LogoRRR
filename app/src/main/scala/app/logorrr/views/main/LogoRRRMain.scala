package app.logorrr.views.main

import app.logorrr.conf.{RecentFileSettings, Settings, SettingsIO, SquareImageSettings, StageSettings}
import app.logorrr.model.{LogEntryInstantFormat, LogFileSettings}
import app.logorrr.util.CanLog
import app.logorrr.views.{Filter, Fltr}
import javafx.application.HostServices
import javafx.scene.layout.BorderPane

import java.nio.file.Path

class LogoRRRMain(hostServices: HostServices
                  , closeStage: => Unit
                  , stageSettings: StageSettings
                  , recentFileSettings: RecentFileSettings) extends BorderPane
  with CanLog {

  val mB = new LogoRRRMenuBar(hostServices, openLogFile, closeAllLogFiles, updateLogReportDefinition, closeStage)
  val ambp = LogoRRRMainBorderPane(hostServices, stageSettings,  initFileMenu)

  init()

  def updateLogReportDefinition(logFileDef: LogFileSettings): Unit = {
    ambp.updateLogFile(logFileDef)
  }

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)

    // reverse since most recently file is saved first in list but should be opened last (= is last log file)
    for ((_, lrd) <- recentFileSettings.logFileDefinitions.toSeq.reverse) {
      addLogFile(lrd)
    }
    // if no report was checked as active (which should be a bug) activate
    // the last one
    recentFileSettings.someActive match {
      case Some(value) => selectLog(value.path)
      case None =>
        logError("No active log file entries found.")
        selectLastLogFile()
    }
    // only after having initialized we activate change listeners */
    ambp.init()
  }

  /** called when 'Open File' is or an entry of 'Recent Files' is selected. */
  def openLogFile(path: Path): Unit = {
    logTrace(s"Try to open log file ${path.toAbsolutePath.toString}")

    if (!ambp.contains(path)) {
      SettingsIO.updateRecentFileSettings(rf => {
        val lrd = LogFileSettings(path.toString
          , LogFileSettings.DefaultDividerPosition
          , Filter.seq
          , Option(LogEntryInstantFormat.Default))
        rf.copy(logFileDefinitions =
          Map(lrd.pathAsString -> lrd) ++ rf.logFileDefinitions)
      })
      addLogFile(LogFileSettings(path))
      selectLog(path)
      initFileMenu()
    } else {
      logTrace("File is already opened.")
    }

  }

  /** removes all log files */
  def closeAllLogFiles(): Unit = {
    shutdown()
    SettingsIO.updateRecentFileSettings(rf => rf.clear())
    initFileMenu()
  }

  private def initFileMenu(): Unit = {
    mB.init()
  }

  def setSceneWidth(sceneWidth: Int): Unit = ambp.setSceneWidth(sceneWidth)

  def addLogFile(lrd: LogFileSettings): Unit = {
    if (!ambp.contains(lrd.path)) {
      ambp.addLogFile(lrd)
    } else {
      logWarn(s"Path ${lrd.path.toAbsolutePath} is already opened ...")
    }
  }

  def selectLog(path: Path): Unit = ambp.selectLog(path)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}
