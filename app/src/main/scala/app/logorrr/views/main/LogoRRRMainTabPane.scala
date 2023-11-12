package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.logfiletab.LogFileTab
import javafx.beans.value.ChangeListener
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

class LogoRRRMainTabPane extends TabPane with CanLog {

  val selectedLogFileTab: ChangeListener[Tab] = JfxUtils.onNew {
    case logFileTab: LogFileTab =>
      logTrace(s"Selected: ${logFileTab.pathAsString}")
      LogoRRRGlobals.setSomeActive(Option(logFileTab.pathAsString))
      // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
      getSelectionModel.select(logFileTab)
    case x => getSelectionModel.select(null)
  }

  init()

  def init(): Unit = {
    setStyle(LogoRRRMainTabPane.BackgroundStyle)
  }


  /**
   * Defines what should happen when a tab is selected
   * */
  def initSelectionListener(): Unit = {
    getSelectionModel.selectedItemProperty().addListener(selectedLogFileTab)
  }


  def add(tab: LogFileTab): Unit = {
    getTabs.add(tab)
    setStyle(null)
  }

  def contains(p: String): Boolean = getLogFileTabs.exists(lr => lr.pathAsString == p)

  def getLogFileTabs: mutable.Seq[LogFileTab] = getTabs.asScala.flatMap {
    _ match {
      case l: LogFileTab => Option(l)
      case _ => None
    }
  }

  /** shutdown all tabs */
  def shutdown(): Unit = {
    getSelectionModel.selectedItemProperty().removeListener(selectedLogFileTab)
    getLogFileTabs.foreach(t => {
      t.shutdown()
      LogoRRRGlobals.removeLogFile(t.pathAsString)
    })
    getTabs.clear()
  }

  def selectLog(pathAsString: String): Unit = {
    getLogFileTabs.find(_.pathAsString == pathAsString) match {
      case Some(value) =>
        logTrace(s"Activated tab for `$pathAsString`.")
        getSelectionModel.select(value)
      case None =>
        logWarn(s"Couldn't find tab with $pathAsString, selecting last tab ...")
        selectLastLogFile()
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast() // select last added file repaint it on selection

}
