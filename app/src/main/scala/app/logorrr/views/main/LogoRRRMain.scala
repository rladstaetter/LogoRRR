package app.logorrr.views.main

import app.logorrr.conf.{LogoRRRGlobals, Settings}
import app.logorrr.model.LogFileSettings
import app.logorrr.util.CanLog
import javafx.scene.layout.BorderPane

import java.nio.file.{Path, Paths}

class LogoRRRMain(closeStage: => Unit
                  , settings: Settings) extends BorderPane
  with CanLog {

  val mB = new LogoRRRMenuBar(openLogFile, closeAllLogFiles, updateLogFileSettings, closeStage)
  val ambp = new LogoRRRMainBorderPane(initFileMenu)

  init()

  def updateLogFileSettings(logFileSetting: LogFileSettings): Unit = {
    ambp.updateLogFileSettings(logFileSetting)
  }

  def init(): Unit = {
    setTop(mB)
    setCenter(ambp)

    LogoRRRGlobals.allLogs().foreach {
      s => addLogFile(s)
    }

    LogoRRRGlobals.getSomeActive() match {
      case Some(value) => selectLog(value)
      case None =>
        logError("No active log file entries found.")
        selectLastLogFile()
    }
    // only after having initialized we activate change listeners */
    ambp.init()
  }

  /** called when 'Open File' is or an entry of 'Recent Files' is selected. */
  def openLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString
    logTrace(s"Try to open log file $pathAsString")

    if (!ambp.contains(pathAsString)) {
      val logFileSettings = LogFileSettings(path)
      LogoRRRGlobals.updateLogFile(logFileSettings)
      addLogFile(logFileSettings)
      selectLog(pathAsString)
      initFileMenu()
    } else {
      logTrace("File is already opened.")
    }

  }

  /** removes all log files */
  def closeAllLogFiles(): Unit = {
    shutdown()
    LogoRRRGlobals.resetLogs()
    initFileMenu()
  }

  private def initFileMenu(): Unit = {
    mB.init()
  }

  def addLogFile(logFileSettings: LogFileSettings): Unit = {
    if (!ambp.contains(logFileSettings.pathAsString)) {
      ambp.addLogFile(logFileSettings)
    } else {
      logWarn(s"Path ${logFileSettings.path.toAbsolutePath} is already opened ...")
    }
  }

  def selectLog(path: String): Unit = ambp.selectLog(path)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}
