package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.{FileId, IoManager}
import app.logorrr.model.{LogEntry, LogFileSettings}
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.UiNodes
import app.logorrr.views.logfiletab.LogFileTab
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
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
  setId(UiNodes.MainTabPane.value)

  val selectedTabListener: ChangeListener[Tab] = JfxUtils.onNew {
    case logFileTab: LogFileTab =>
      logTrace(s"Selected: '${logFileTab.fileId.value}'")
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
          dropDirectory(path)
        } else if (IoManager.isZip(path)) {
          openZipFile(path)
        } else {
          openFile(FileId(path))
        }
      }
    })
  }

  // by default read all files which are contained in the zip file

  /**
   * Unzips given path, interprets contents as log files and adds them to the GUI
   *
   * @param path zip file to open
   */
  def openZipFile(path: Path): Unit = {
    if (IoManager.isZip(path)) {
      IoManager.unzip(path).foreach {
        case (fileId, entries) =>
          if (!contains(fileId)) {
            addEntriesFromZip(LogFileSettings(fileId), entries)
            selectFile(fileId)
          } else {
            logTrace(s"${fileId.absolutePathAsString} is already opened, selecting tab ...")
            selectFile(fileId)
          }
      }
    } else {
      logWarn(s"Tried to open file as zip, but filename was: '${path.toAbsolutePath.toString}'.")
    }
  }

  private def dropDirectory(path: Path): Unit = {
    Files.list(path).filter((p: Path) => Files.isRegularFile(p)).forEach((t: Path) => openFile(FileId(t)))
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

  def selectFile(fileId: FileId): LogFileTab = {
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

  private def openFile(fileId: FileId): Unit = {
    if (Files.exists(fileId.asPath)) {
      if (!contains(fileId)) {
        addFile(fileId)
      } else {
        logTrace(s"${fileId.absolutePathAsString} is already opened, selecting tab ...")
        selectFile(fileId)
      }
    } else {
      logWarn(s"${fileId.absolutePathAsString} does not exist.")
    }
  }

  def addFile(fileId: FileId): Unit = {
    val logFileSettings = LogFileSettings(fileId)
    LogoRRRGlobals.registerSettings(logFileSettings)
    val entries = IoManager.readEntries(logFileSettings.path, logFileSettings.someLogEntryInstantFormat)
    addLogFileTab(LogFileTab(LogoRRRGlobals.getLogFileSettings(fileId), entries))
    selectFile(fileId)
  }

  def addEntriesFromZip(logFileSettings: LogFileSettings, entries: ObservableList[LogEntry]): LogFileTab = timeR({
    LogoRRRGlobals.registerSettings(logFileSettings)
    val tab = LogFileTab(LogoRRRGlobals.getLogFileSettings(logFileSettings.fileId), entries)
    addLogFileTab(tab)
    tab
  }, s"Added ${logFileSettings.fileId.absolutePathAsString} to TabPane")

  /** Adds a new logfile to display and initializes bindings and listeners */
  def addLogFileTab(tab: LogFileTab): Unit = {
    JfxUtils.execOnUiThread({
      tab.init()
      getTabs.add(tab)
      tab.initContextMenu()
    })
  }

}
