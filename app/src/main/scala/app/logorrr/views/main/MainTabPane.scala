package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogFileSettings
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.logfiletab.LogFileTab
import javafx.beans.value.ChangeListener
import javafx.scene.control.{Tab, TabPane}
import javafx.scene.input.{DragEvent, TransferMode}

import java.nio.file.{Files, Path}
import scala.collection.mutable
import scala.jdk.CollectionConverters._

object MainTabPane {

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/drop-files-here.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: BEIGE;
      |-fx-background-size: 100%;
      |""".stripMargin

}

class MainTabPane extends TabPane with CanLog {

  setStyle(MainTabPane.BackgroundStyle)

  val selectedLogFileTab: ChangeListener[Tab] = JfxUtils.onNew {
    case logFileTab: LogFileTab =>
      logTrace(s"Selected: ${logFileTab.pathAsString}")
      LogoRRRGlobals.setSomeActive(Option(logFileTab.pathAsString))
      // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
      getSelectionModel.select(logFileTab)
    case x => getSelectionModel.select(null)
  }


  def init(): Unit = {
    initDnD()
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

  def selectLog(pathAsString: String): LogFileTab = {
    logTrace(s"selecting tab `$pathAsString` ...")
    getLogFileTabs.find(_.pathAsString == pathAsString) match {
      case Some(logFileTab) =>
        logTrace(s"Activated tab for `$pathAsString`.")
        getSelectionModel.select(logFileTab)
        logFileTab
      case None =>
        logWarn(s"Couldn't find tab with $pathAsString, selecting last tab ...")
        selectLastLogFile()
        getTabs.get(getTabs.size() - 1).asInstanceOf[LogFileTab]
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast() // select last added file repaint it on selection


  private def initDnD(): Unit = {
    /** needed to activate drag'n drop */
    setOnDragOver((event: DragEvent) => {
      if (event.getDragboard.hasFiles) {
        event.acceptTransferModes(TransferMode.ANY: _*)
      }
    })

    /** try to interpret dropped element as log file, activate view */
    setOnDragDropped((event: DragEvent) => {
      for (f <- event.getDragboard.getFiles.asScala) {
        val path = f.toPath
        if (Files.isDirectory(path)) {
          Files.list(path).filter((p: Path) => Files.isRegularFile(p)).forEach((t: Path) => dropLogFile(t))
        } else dropLogFile(path)
      }
    })
  }

  private def dropLogFile(path: Path): Unit = {
    val pathAsString = path.toAbsolutePath.toString

    if (Files.exists(path)) {
      if (!contains(pathAsString)) {
        addLogFile(path)
      } else {
        logTrace(s"$pathAsString is already opened, selecting tab ...")
        selectLog(pathAsString)
      }
    } else {
      logWarn(s"$pathAsString does not exist.")
    }
  }

  def addLogFile(path: Path): Unit = {
    val logFileSettings = LogFileSettings(path)
    LogoRRRGlobals.registerSettings(logFileSettings)

    val tab = new LogFileTab(LogoRRRGlobals.getLogFileSettings(logFileSettings.pathAsString), logFileSettings.readEntries())
    addLogFileTab(tab)
    tab.init()
    selectLog(path.toAbsolutePath.toString)
  }


  /** Adds a new logfile to display */
  def addLogFileTab(tab: LogFileTab): Unit = JfxUtils.execOnUiThread(add(tab))


}
