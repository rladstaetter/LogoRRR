package app.logorrr.views.main

import app.logorrr.conf.{Settings, SettingsIO}
import app.logorrr.model.{LogEntrySetting, LogFileDefinition}
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

  val mB = new LogoRRRMenuBar(hostServices, openLogFile, closeAllLogFiles, updateLogReportDefinition, closeStage)
  val ambp = AppMainBorderPane(hostServices, settings, initFileMenu)

  init()

  def updateLogReportDefinition(logFileDef: LogFileDefinition): Unit = {
    ambp.updateLogFile(logFileDef)
  }

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)

    // reverse since most recently file is saved first in list but should be opened last (= is last log file)
    for ((p, lrd) <- settings.recentFiles.logFileDefinitions.toSeq.reverse) {
      addLogFile(lrd)
    }
    // if no report was checked as active (which should be a bug) activate
    // the last one
    settings.recentFiles.someActive match {
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
        val lrd = LogFileDefinition(path.toString
          , true
          , LogFileDefinition.DefaultDividerPosition
          , Filter.seq
          , Option(LogEntrySetting.Default))
        rf.copy(logFileDefinitions =
          Map(lrd.pathAsString -> lrd) ++ rf.logFileDefinitions)
      })
      addLogFile(LogFileDefinition(path))
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

  def addLogFile(lrd: LogFileDefinition): Unit = {
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
