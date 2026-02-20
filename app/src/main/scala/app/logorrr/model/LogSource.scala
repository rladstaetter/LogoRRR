package app.logorrr.model

import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.conf.{FileId, LogFileSettings, LogoRRRGlobals, SearchTermGroup}
import app.logorrr.io.IoManager
import app.logorrr.util.DndUtil
import javafx.collections.ObservableList
import javafx.scene.input.DragEvent
import net.ladstatt.util.log.TinyLog

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.jdk.CollectionConverters.*

class LogSource(globalSearchTermGroups: ObservableList[MutSearchTermGroup]
                , settings: Seq[LogFileSettings]
                , someActiveFile: Option[FileId]
                , val ui: UiTarget) extends TinyLog:

  /** setup Drag'n drop */
  ui.setOnDragOver(DndUtil.onDragAcceptAll)
  ui.setOnDragDropped(onDragDropped)

  def loadLogFiles(): Unit =
    if (settings.isEmpty)
      ()
    else
      val (zipSettings, fileSettings) = settings.partition(p => p.fileId.isZipEntry)
      val zipSettingsMap: Map[FileId, LogFileSettings] = zipSettings.map(s => s.fileId -> s).toMap

      val zips: Map[FileId, Seq[FileId]] = FileId.reduceZipFiles(zipSettingsMap.keys.toSeq)

      val futures: Future[Seq[Option[LogorrrModel]]] = Future.sequence:

        // load zip files also in parallel
        val zipFutures: Seq[Future[Option[LogorrrModel]]] =
          zips.keys.toSeq.flatMap(f => {
            timeR({
              IoManager.unzip(f.asPath, zips(f).toSet).map {
                // only if settings contains given fileId - user could have removed it by closing the tab - load this file
                case (fileId, entries) =>
                  Future {
                    if zipSettingsMap.contains(fileId) then {
                      val settingz = zipSettingsMap(fileId)
                      LogoRRRGlobals.registerSettings(settingz)
                      Option(LogorrrModel(LogoRRRGlobals.getLogFileSettings(fileId), entries))
                    } else None
                  }
              }
            }, s"Loaded zip file '${f.absolutePathAsString}'.")
          })

        val fileBasedSettings: Seq[Future[Option[LogorrrModel]]] = fileSettings.map(lfs => Future {
          timeR({
            val entries: ObservableList[LogEntry] = IoManager.readEntries(lfs.path, lfs.someTimestampSettings)
            val mutLogFileSettings = LogoRRRGlobals.getLogFileSettings(lfs.fileId)
            Option(LogorrrModel(mutLogFileSettings, entries))
          }, s"Loaded '${lfs.fileId.absolutePathAsString}' from filesystem ...")
        })
        zipFutures ++ fileBasedSettings

      val models = Await.result(futures, Duration.Inf).flatten

      models.foreach(model => ui.addData(LogorrrModel(model.mutLogFileSettings, model.entries)))
      someActiveFile match {
        case Some(value) if ui.contains(value) => ui.selectFile(value)
        case _ => ui.selectLastLogFile()
      }

  private def onDragDropped(event: DragEvent): Unit =
    event.getDragboard.getFiles.forEach(f => {
      val path = f.toPath
      if Files.isDirectory(path) then {
        addDirectory(path)
      } else if IoManager.isZip(path) then {
        addZip(path)
      } else {
        addFile(path)
      }
    })

  def openFile(fileId: FileId): Unit = {
    if (ui.contains(fileId)) {
      ui.selectFile(fileId)
    } else if (!IoManager.isZip(fileId.asPath)) {
      addFileId(fileId)
    } else {
      addZip(fileId.asPath)
    }
  }

  def addDirectory(path: Path): Unit =
    val files = Files.list(path).filter((p: Path) => Files.isRegularFile(p))
    val collectorResults: java.util.Map[java.lang.Boolean, java.util.List[Path]] = files.collect(Collectors.partitioningBy((p: Path) => IoManager.isZip(p)))
    collectorResults.get(false).forEach(p => addFile(p))
    collectorResults.get(true).forEach(p => addZip(p))

  private def mkSearchTermGroup: SearchTermGroup = {
    val groups = LogoRRRGlobals.searchTermGroupEntries.filtered(_.isSelected)
    if groups.isEmpty then
      SearchTermGroup(Seq(), true)
    else
      groups.get(0).mkImmutable()
  }

  def addZip(path: Path): Unit =
    IoManager.unzip(path).foreach:
      case (fileId, entries) =>
        if !ui.contains(fileId) then
          val settings = LogFileSettings.mk(fileId, mkSearchTermGroup)
          addEntries(settings, entries)
        else ui.selectFile(fileId)

  def addFile(path: Path): Unit =
    val fileId = FileId(path)
    if !ui.contains(fileId) then addFileId(fileId)
    else ui.selectFile(fileId)

  def addFileId(fileId: FileId): Unit = timeR({
    val settings = LogFileSettings.mk(fileId, mkSearchTermGroup)
    val entries = IoManager.readEntries(settings.path, settings.someTimestampSettings)
    addEntries(settings, entries)
  }, s"addFile ${fileId.absolutePathAsString}")

  private def addEntries(settings: LogFileSettings, entries: ObservableList[LogEntry]): Unit =
    val fileId = settings.fileId
    LogoRRRGlobals.registerSettings(settings)
    ui.addData(LogorrrModel(LogoRRRGlobals.getLogFileSettings(fileId), entries))
    ui.selectFile(fileId)

  def closeAllLogFiles(): Unit =
    ui.shutdown()
    LogoRRRGlobals.clearLogFileSettings()
