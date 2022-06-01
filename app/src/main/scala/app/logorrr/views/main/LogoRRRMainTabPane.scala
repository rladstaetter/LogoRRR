package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.LogFileTab
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.{Tab, TabPane}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object LogoRRRMainTabPane {

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/drop-files-here.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: BEIGE;
      |-fx-background-size: 100%;
      |""".stripMargin


}

class LogoRRRMainTabPane()
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
            logTrace("Selected: " + logFileTab.pathAsString)
            LogoRRRGlobals.setSomeActive(Option(logFileTab.pathAsString))
            // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
            getSelectionModel.select(logFileTab)
          case _ =>
        }
    })
  }

  def add(tab: LogFileTab): Unit = getTabs.add(tab)


  def contains(p: String): Boolean = getLogFileTabs.exists(lr => lr.pathAsString == p)

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
    getLogFileTabs.find(_.pathAsString == pathAsString) match {
      case Some(value) =>
        logTrace(s"Selects tab view with path ${pathAsString}.")
        getSelectionModel.select(value)
      case None =>
        logWarn(s"Couldn't find tab with ${pathAsString}, selecting last tab ...")
        selectLastLogFile()
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast() // select last added file repaint it on selection

}
