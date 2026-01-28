package app.logorrr.views.main

import app.logorrr.conf.{DefaultSearchTermGroups, FileId, LogFileSettings, LogoRRRGlobals}
import app.logorrr.io.IoManager
import app.logorrr.model.{LogEntry, LogorrrModel}
import app.logorrr.util.DndUtil
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.logfiletab.LogFileTab
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.scene.control.TabPane
import javafx.scene.input.DragEvent
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


class MainTabPane(dstg: DefaultSearchTermGroups) extends TabPane with TinyLog:

  // -- bindings
  styleProperty().bind(Bindings.createStringBinding(() => MainTabPane.BackgroundStyle))
  idProperty().bind(Bindings.createStringBinding(() => UiNodes.MainTabPane.value))

  LogoRRRGlobals.mutSettings.someActiveLogProperty.bind(Bindings.createObjectBinding[Option[FileId]](() => {
    getSelectionModel.getSelectedItem match {
      case tab: LogFileTab => Option(tab.getFileId)
      case _ => None
    }
  }, getSelectionModel.selectedItemProperty()))


  /** register actions which are to be executed on a drop */
  def onDragDropped(event: DragEvent): Unit =
    for f <- event.getDragboard.getFiles.asScala do {
      val path = f.toPath
      if Files.isDirectory(path) then {
        processDirectory(path, dstg)
      } else if IoManager.isZip(path) then {
        openZipFile(path, dstg)
      } else {
        openRegularFile(path, dstg)
      }
    }


  /** setup drag'n drop for all modes */
  setOnDragOver(DndUtil.onDragAcceptAll)

  /** try to interpret dropped element as log file, activate view */
  setOnDragDropped(onDragDropped)

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
            addEntries(fileId, LogFileSettings.mk(fileId, dstg), entries)
          else selectFile(fileId)
    else logWarn(s"Tried to open file as zip, but filename was: '${path.toAbsolutePath.toString}'.")

  /** open all regular files in a directory, do not recurse */
  private def processDirectory(path: Path, dstg: DefaultSearchTermGroups): Unit =
    val files = Files.list(path).filter((p: Path) => Files.isRegularFile(p))
    // diff between regular files and zip files, try to open zip files as container
    val collectorResults: util.Map[lang.Boolean, util.List[Path]] = files.collect(Collectors.partitioningBy((p: Path) => p.getFileName.toString.endsWith(".zip")))
    collectorResults.get(false).forEach(p => openRegularFile(p, dstg))
    collectorResults.get(true).forEach(p => openZipFile(p, dstg))

  /**
   * Defines what should happen when a tab is selected
   * */

  def contains(p: FileId): Boolean = getLogFileTabs.exists(lr => lr.getFileId == p)

  def getByFileId(fileId: FileId): Option[LogFileTab] = getLogFileTabs.find(_.getFileId == fileId)

  def getLogFileTabs: mutable.Seq[LogFileTab] = getTabs.asScala.flatMap:
    case l: LogFileTab => Option(l)
    case _ => None

  def selectFile(fileId: FileId): LogFileTab =
    getByFileId(fileId) match
      case Some(logFileTab) =>
        getSelectionModel.select(logFileTab)
        logFileTab
      case None =>
        logWarn(s"Couldn't find '${fileId.value}', selecting last tab ...")
        selectLastLogFile()
        getTabs.get(getTabs.size() - 1).asInstanceOf[LogFileTab]

  def selectLastLogFile(): Unit = getSelectionModel.selectLast()

  private def openRegularFile(path: Path, dstg: DefaultSearchTermGroups): Unit =
    val fileId = FileId(path)
    if Files.exists(path) then
      if !contains(fileId) then
        addFile(fileId, dstg)
      else
        logTrace(s"${fileId.absolutePathAsString} is already opened, selecting tab ...")
        selectFile(fileId)
    else logWarn(s"${fileId.absolutePathAsString} does not exist.")

  def addFile(fileId: FileId, dstg: DefaultSearchTermGroups): Unit =
    val logFileSettings = LogFileSettings.mk(fileId, dstg)
    val entries = IoManager.readEntries(logFileSettings.path, logFileSettings.someTimestampSettings)
    addEntries(fileId, logFileSettings, entries)

  private def addEntries(fileId: FileId, logFileSettings: LogFileSettings, entries: ObservableList[LogEntry]): LogFileTab =
    LogoRRRGlobals.registerSettings(logFileSettings)
    val model = LogorrrModel(LogoRRRGlobals.getLogFileSettings(fileId), entries)
    addData(model)
    selectFile(fileId)

  /** Adds a new logfile to display and initializes bindings and listeners */
  def addData(model: LogorrrModel): Unit =
    val tab = LogFileTab(model)
    tab.init()
    getTabs.add(tab)
    tab.initContextMenu()

  def shutdown(): Unit =
    LogoRRRGlobals.mutSettings.someActiveLogProperty.unbind()
    styleProperty().unbind()
    idProperty().unbind()
    //  getSelectionModel.selectedItemProperty().removeListener(selectedTabListener)
    getLogFileTabs.foreach(_.shutdown())
    getTabs.clear()

