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
  // leave here otherwise css rendering breaks
  setStyle(MainTabPane.BackgroundStyle)

  val selectedTabListener: ChangeListener[Tab] = JfxUtils.onNew {
    case logFileTab: LogFileTab =>
      logTrace(s"Selected: '${logFileTab.pathAsString}'")
      LogoRRRGlobals.setSomeActiveLogFile(Option(logFileTab.pathAsString))
      // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
      getSelectionModel.select(logFileTab)
    case _ => getSelectionModel.select(null)
  }

  def init(): Unit = {
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

  /**
   * Defines what should happen when a tab is selected
   * */
  def initSelectionListener(): Unit = {
    getSelectionModel.selectedItemProperty().addListener(selectedTabListener)
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
    getSelectionModel.selectedItemProperty().removeListener(selectedTabListener)
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()
  }

  def selectLog(pathAsString: String): LogFileTab = {
    getLogFileTabs.find(_.pathAsString == pathAsString) match {
      case Some(logFileTab) =>
        logTrace(s"Activated tab for '$pathAsString'.")
        getSelectionModel.select(logFileTab)
        logFileTab
      case None =>
        logWarn(s"Couldn't find '$pathAsString', selecting last tab ...")
        selectLastLogFile()
        getTabs.get(getTabs.size() - 1).asInstanceOf[LogFileTab]
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast()

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

    addLogFileTab(new LogFileTab(LogoRRRGlobals.getLogFileSettings(logFileSettings.pathAsString), logFileSettings.readEntries()))
    selectLog(path.toAbsolutePath.toString)
  }


  /** Adds a new logfile to display and initializes bindings and listeners */
  def addLogFileTab(tab: LogFileTab): Unit = {
    tab.init()
    getTabs.add(tab)
    tab.initContextMenu()
  }

}
