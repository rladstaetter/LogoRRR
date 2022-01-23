package app.logorrr.views

import app.logorrr.conf.SettingsIO
import app.logorrr.model.{LogReport, LogReportDefinition}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.main.AppMainBorderPane
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.{Tab, TabPane}

import java.nio.file.Path
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object LogViewTabPane {

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/save-as.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: BEIGE;
      |-fx-background-size: 100%;
      |""".stripMargin

  /** constructor to pass parent and do binding */
  def apply(parent: AppMainBorderPane
            , initFileMenu: => Unit): LogViewTabPane = {
    val lvtp = new LogViewTabPane(initFileMenu)
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp.squareWidthProperty.bind(parent.squareWidthProperty)
    lvtp
  }

}

class LogViewTabPane(initFileMenu: => Unit)
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
          case logReportTab: LogReportTab =>
            logTrace("Selected: " + logReportTab.logReport.logFileDefinition.path)
            SettingsIO.updateActiveLogFile(logReportTab.logReport.path)
            // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
            getSelectionModel.select(logReportTab)
            logReportTab.repaint()
          case _ =>
        }
    })
  }

  def add(logReport: LogReport): Unit = getTabs.add(LogReportTab(this, logReport, initFileMenu))

  def contains(p: Path): Boolean = getLogReportTabs.exists(lr => lr.logReport.path.toAbsolutePath.toString == p.toAbsolutePath.toString)

  private def getLogReportTabs: mutable.Seq[LogReportTab] = getTabs.asScala.flatMap {
    case t => t match {
      case l: LogReportTab => Option(l)
      case _ => None
    }
  }

  /** shutdown all tabs */
  def shutdown(): Unit = {
    getLogReportTabs.foreach(_.shutdown())
    getTabs.clear()
  }

  def selectLog(path: Path): Unit = {
    getLogReportTabs.find(_.logReport.path == path) match {
      case Some(value) =>
        logTrace(s"Selects tab view with path ${path.toAbsolutePath.toString}.")
        getSelectionModel.select(value)
      case None =>
        logWarn(s"Couldn't find tab with ${path.toAbsolutePath.toString}, selecting last tab ...")
        selectLastLogFile()
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast() // select last added file repaint it on selection


  def addLogReport(lrd: LogReportDefinition): Unit = {
    if (lrd.isPathValid) {
      Try(LogReport(lrd)) match {
        case Success(logReport) =>
          logInfo(s"Opening ${lrd.path.toAbsolutePath.toString} ... ")
          add(logReport)
        case Failure(exception) => logError("Could not import file " + lrd.path.toAbsolutePath + ", reason: " + exception.getMessage)
      }
    } else {
      logWarn(s"Could not read ${lrd.path.toAbsolutePath} ...")
    }
  }
}
