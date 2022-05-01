package app.logorrr.views

import app.logorrr.conf.{LogoRRRGlobals, SettingsIO}
import app.logorrr.model.{LogEntry, LogEntryFileReader, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.main.LogoRRRMainBorderPane
import javafx.application.HostServices
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.scene.control.{Tab, TabPane}

import java.nio.file.Path
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object LogoRRRMainTabPane {

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/drop-files-here.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: BEIGE;
      |-fx-background-size: 100%;
      |""".stripMargin

  /** constructor to pass parent and do binding */
  def apply(parent: LogoRRRMainBorderPane
            , initFileMenu: => Unit): LogoRRRMainTabPane = {
    val lvtp = new LogoRRRMainTabPane(initFileMenu)
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp
  }

}

class LogoRRRMainTabPane(initFileMenu: => Unit)
  extends TabPane
    with CanLog {

  /** bound to sceneWidthProperty of parent logorrrMainBorderPane */
  val sceneWidthProperty = new SimpleIntegerProperty()

  setStyle(LogoRRRMainTabPane.BackgroundStyle)

  /**
   * Defines what should happen when a tab is selected
   * */
  def init(): Unit = {
    getSelectionModel.selectedItemProperty().addListener(JfxUtils.onNew {
      t1: Tab =>
        t1 match {
          case logFileTab: LogFileTab =>
            logTrace("Selected: " + logFileTab.initialLogFileSettings.path)
            LogoRRRGlobals.setSomeActive(Option(logFileTab.initialLogFileSettings.pathAsString))
            // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
            getSelectionModel.select(logFileTab)
            logFileTab.repaint()
          case _ =>
        }
    })
  }

  def add(logEntries: ObservableList[LogEntry]
          , logFileSettings: LogFileSettings): Unit = {
    val tab = LogFileTab(this, logEntries, logFileSettings, initFileMenu)
    getTabs.add(tab)
  }

  def contains(p: String): Boolean = getLogFileTabs.exists(lr => lr.initialLogFileSettings.pathAsString == p)

  private def getLogFileTabs: mutable.Seq[LogFileTab] = getTabs.asScala.flatMap {
    t =>
      t match {
        case l: LogFileTab => Option(l)
        case _ => None
      }
  }

  /** shutdown all tabs */
  def shutdown(): Unit = {
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()
  }

  def selectLog(pathAsString: String): Unit = {
    getLogFileTabs.find(_.initialLogFileSettings.pathAsString == pathAsString) match {
      case Some(value) =>
        logTrace(s"Selects tab view with path ${pathAsString}.")
        getSelectionModel.select(value)
      case None =>
        logWarn(s"Couldn't find tab with ${pathAsString}, selecting last tab ...")
        selectLastLogFile()
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast() // select last added file repaint it on selection

  def addLogFile(logFileSettings: LogFileSettings): Unit = {
    if (logFileSettings.isPathValid) {
      Try(logFileSettings.someLogEntrySetting match {
        case Some(value) => LogEntryFileReader.from(logFileSettings.path, logFileSettings.filters, value)
        case None => LogEntryFileReader.from(logFileSettings.path, logFileSettings.filters)
      }) match {
        case Success(logEntries) =>
          logInfo(s"Opening ${logFileSettings.path.toAbsolutePath.toString} ... ")
          add(logEntries, logFileSettings)
        case Failure(ex) =>
          val msg = s"Could not import file ${logFileSettings.path.toAbsolutePath}"
          logException(msg, ex)
      }
    } else {
      logWarn(s"Could not read ${logFileSettings.path.toAbsolutePath} - does it exist?")
    }
  }
}
