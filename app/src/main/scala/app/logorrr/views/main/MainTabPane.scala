package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
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
      logTrace(s"Selected: '${logFileTab.fileId}'")
      LogoRRRGlobals.setSomeActiveLogFile(Option(logFileTab.fileId))
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

  def contains(p: FileId): Boolean = getLogFileTabs.exists(lr => lr.fileId == p)

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

  def selectLog(fileId: FileId): LogFileTab = {
    getLogFileTabs.find(_.fileId == fileId) match {
      case Some(logFileTab) =>
        logTrace(s"Activated tab for '$fileId'.")
        getSelectionModel.select(logFileTab)
        logFileTab
      case None =>
        logWarn(s"Couldn't find '$fileId', selecting last tab ...")
        selectLastLogFile()
        getTabs.get(getTabs.size() - 1).asInstanceOf[LogFileTab]
    }
  }

  def selectLastLogFile(): Unit = getSelectionModel.selectLast()

  private def dropLogFile(path: Path): Unit = {
    val fileId = FileId(path)

    if (Files.exists(path)) {
      if (!contains(fileId)) {
        addLogFile(path)
      } else {
        logTrace(s"${fileId.absolutePathAsString} is already opened, selecting tab ...")
        selectLog(fileId)
      }
    } else {
      logWarn(s"${fileId.absolutePathAsString} does not exist.")
    }
  }

  def addLogFile(path: Path): Unit = {
    val fileId = FileId(path)
    val logFileSettings = LogFileSettings(fileId)
    LogoRRRGlobals.registerSettings(logFileSettings)

    addLogFileTab(new LogFileTab(LogoRRRGlobals.getLogFileSettings(fileId), logFileSettings.readEntries()))
    selectLog(fileId)
  }


  /** Adds a new logfile to display and initializes bindings and listeners */
  def addLogFileTab(tab: LogFileTab): Unit = {
    tab.init()
    getTabs.add(tab)
    tab.initContextMenu()
  }

}
