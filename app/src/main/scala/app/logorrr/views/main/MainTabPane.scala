package app.logorrr.views.main

import app.logorrr.conf.{DefaultSearchTermGroups, FileId, LogFileSettings, LogoRRRGlobals}
import app.logorrr.io.IoManager
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.LogFileTab
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.{Tab, TabPane}
import javafx.scene.input.{DragEvent, TransferMode}
import net.ladstatt.util.log.TinyLog

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import java.{lang, util}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object MainTabPane:

  private val BackgroundStyle: String =
    """
      |-fx-background-image: url(/app/logorrr/drop-files-here.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: linear-gradient(to bottom, #f5f5dc, #d2b48c);
      |-fx-background-size: auto;
      |""".stripMargin


class MainTabPane extends TabPane with TinyLog:
  // leave here otherwise css rendering breaks
  setStyle(MainTabPane.BackgroundStyle)
  setId(UiNodes.MainTabPane.value)

  // setup DnD
  setOnDragOver((event: DragEvent) => {
    if event.getDragboard.hasFiles then {
      event.acceptTransferModes(TransferMode.ANY *)
    }
  })
  
  def dstg = DefaultSearchTermGroups(LogoRRRGlobals.getSettings.searchTermGroups)

  /** try to interpret dropped element as log file, activate view */
  setOnDragDropped((event: DragEvent) => {
    for f <- event.getDragboard.getFiles.asScala do {
      val path = f.toPath
      if Files.isDirectory(path) then {
        dropDirectory(path, dstg)
      } else if IoManager.isZip(path) then {
        openZipFile(path, dstg)
      } else {
        openFile(FileId(path), dstg)
      }
    }
  })

  private val selectedTabListener: ChangeListener[Tab] = JfxUtils.onNew:
    case logFileTab: LogFileTab =>
      logTrace(s"Selected: '${logFileTab.fileId.value}'")
      LogoRRRGlobals.setSomeActiveLogFile(Option(logFileTab.fileId))
      // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
      getSelectionModel.select(logFileTab)
    case _ => getSelectionModel.select(null)


  /**
   * Unzips given path, interprets contents as log files and adds them to the GUI
   *
   * @param path zip file to open
   */
  def openZipFile(path: Path, dstg: DefaultSearchTermGroups): Unit =
    if IoManager.isZip(path) then
      IoManager.unzip(path).foreach:
        case (fileId, entries) =>
          if !contains(fileId) then
            addEntriesFromZip(LogFileSettings.mk(fileId, dstg), entries)
            selectFile(fileId)
          else
            logTrace(s"${fileId.absolutePathAsString} is already opened, selecting tab ...")
            selectFile(fileId)
    else
      logWarn(s"Tried to open file as zip, but filename was: '${path.toAbsolutePath.toString}'.")

  private def dropDirectory(path: Path, dstg: DefaultSearchTermGroups): Unit =
    val files = Files.list(path).filter((p: Path) => Files.isRegularFile(p))
    // diff between regular files and zip files, try to open zip files as container
    val collectorResults: util.Map[lang.Boolean, util.List[Path]] = files.collect(Collectors.partitioningBy((p: Path) => p.getFileName.toString.endsWith(".zip")))
    collectorResults.get(false).forEach(p => openFile(FileId(p), dstg))
    collectorResults.get(true).forEach(p => openZipFile(p, dstg))

  /**
   * Defines what should happen when a tab is selected
   * */
  def initSelectionListener(): Unit =
    getSelectionModel.selectedItemProperty().addListener(selectedTabListener)

  def contains(p: FileId): Boolean =
    getLogFileTabs.exists(lr => lr.fileId == p)

  def getByFileId(fileId: FileId): Option[LogFileTab] = getLogFileTabs.find(_.fileId == fileId)

  def getLogFileTabs: mutable.Seq[LogFileTab] = getTabs.asScala.flatMap:
    case l: LogFileTab => Option(l)
    case _ => None

  /** shutdown all tabs */
  def shutdown(): Unit =
    getSelectionModel.selectedItemProperty().removeListener(selectedTabListener)
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()

  def selectFile(fileId: FileId): LogFileTab =
    getLogFileTabs.find(_.fileId == fileId) match
      case Some(logFileTab) =>
        logTrace(s"Activated tab for '${fileId.value}'.")
        getSelectionModel.select(logFileTab)
        logFileTab
      case None =>
        logWarn(s"Couldn't find '${fileId.value}', selecting last tab ...")
        selectLastLogFile()
        getTabs.get(getTabs.size() - 1).asInstanceOf[LogFileTab]

  def selectLastLogFile(): Unit = getSelectionModel.selectLast()

  private def openFile(fileId: FileId, dstg: DefaultSearchTermGroups): Unit =
    if Files.exists(fileId.asPath) then
      if !contains(fileId) then
        addFile(fileId, dstg)
      else
        logTrace(s"${fileId.absolutePathAsString} is already opened, selecting tab ...")
        selectFile(fileId)
    else
      logWarn(s"${fileId.absolutePathAsString} does not exist.")

  def addFile(fileId: FileId, dstg: DefaultSearchTermGroups): Unit =
    val logFileSettings = LogFileSettings.mk(fileId, dstg)
    LogoRRRGlobals.registerSettings(logFileSettings)
    val entries = IoManager.readEntries(logFileSettings.path, logFileSettings.someTimestampSettings)
    addLogFileTab(LogFileTab(LogoRRRGlobals.getLogFileSettings(fileId), entries))
    selectFile(fileId)

  private def addEntriesFromZip(logFileSettings: LogFileSettings, entries: ObservableList[LogEntry]): LogFileTab = timeR({
    LogoRRRGlobals.registerSettings(logFileSettings)
    val tab = LogFileTab(LogoRRRGlobals.getLogFileSettings(logFileSettings.fileId), entries)
    addLogFileTab(tab)
    tab
  }, s"Added ${logFileSettings.fileId.absolutePathAsString} to TabPane")

  /** Adds a new logfile to display and initializes bindings and listeners */
  def addLogFileTab(tab: LogFileTab): Unit =
    tab.init()
    getTabs.add(tab)
    tab.initContextMenu()

