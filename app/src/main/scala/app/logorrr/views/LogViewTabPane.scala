package app.logorrr.views

import app.logorrr.conf.SettingsIO
import app.logorrr.model.{LogEntries, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.main.AppMainBorderPane
import javafx.application.HostServices
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.{Tab, TabPane}

import java.nio.file.Path
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object LogViewTabPane {

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/drop-files-here.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: BEIGE;
      |-fx-background-size: 100%;
      |""".stripMargin

  /** constructor to pass parent and do binding */
  def apply(hostServices: HostServices
            , parent: AppMainBorderPane
            , initFileMenu: => Unit): LogViewTabPane = {
    val lvtp = new LogViewTabPane(hostServices, initFileMenu)
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp.squareWidthProperty.bind(parent.squareWidthProperty)
    lvtp
  }

}

class LogViewTabPane(hostServices: HostServices
                     , initFileMenu: => Unit)
  extends TabPane
    with CanLog {

  /** bound to sceneWidthProperty of parent logorrrMainBorderPane */
  val sceneWidthProperty = new SimpleIntegerProperty()

  /** bound to squareWidthProperty of parent logorrrMainBorderPane */
  val squareWidthProperty = new SimpleIntegerProperty()

  setStyle(LogViewTabPane.BackgroundStyle)

  /** init change listeners */
  def init(): Unit = {
    getSelectionModel.selectedItemProperty().addListener(JfxUtils.onNew {
      t1: Tab =>
        t1 match {
          case logFileTab: LogFileTab =>
            logTrace("Selected: " + logFileTab.initialLogFileDefinition.path)
            SettingsIO.updateActiveLogFile(logFileTab.initialLogFileDefinition.path)
            // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
            getSelectionModel.select(logFileTab)
            logFileTab.repaint()
          case _ =>
        }
    })
  }

  def add(logFile: LogEntries
          , logFileDefinition: LogFileSettings): Unit = {
    val tab = LogFileTab(hostServices, this, logFile, logFileDefinition, initFileMenu)
    getTabs.add(tab)
  }

  def contains(p: Path): Boolean = getLogFileTabs.exists(lr => lr.initialLogFileDefinition.path.toAbsolutePath.toString == p.toAbsolutePath.toString)

  private def getLogFileTabs: mutable.Seq[LogFileTab] = getTabs.asScala.flatMap {
    case t => t match {
      case l: LogFileTab => Option(l)
      case _ => None
    }
  }

  /** shutdown all tabs */
  def shutdown(): Unit = {
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()
  }

  def selectLog(path: Path): Unit = {
    getLogFileTabs.find(_.initialLogFileDefinition.path == path) match {
      case Some(value) =>
        logTrace(s"Selects tab view with path ${path.toAbsolutePath.toString}.")
        getSelectionModel.select(value)
      case None =>
        logWarn(s"Couldn't find tab with ${path.toAbsolutePath.toString}, selecting last tab ...")
        selectLastLogFile()
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast() // select last added file repaint it on selection

  def addLogFile(logFileDefinition: LogFileSettings): Unit = {
    if (logFileDefinition.isPathValid) {
      Try(logFileDefinition.someLogEntrySetting match {
        case Some(value) => LogEntries(logFileDefinition.path, value)
        case None => LogEntries(logFileDefinition.path)
      }) match {
        case Success(logFile) =>
          logInfo(s"Opening ${logFileDefinition.path.toAbsolutePath.toString} ... ")
          add(logFile, logFileDefinition)
        case Failure(ex) =>
          val msg = s"Could not import file ${logFileDefinition.path.toAbsolutePath}"
          logException(msg, ex)
      }
    } else {
      logWarn(s"Could not read ${logFileDefinition.path.toAbsolutePath} - does it exist?")
    }
  }
}
