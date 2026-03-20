package app.logorrr.model

import app.logorrr.conf.mut.{MutLogFileSettings, MutSearchTermGroup}
import app.logorrr.conf.{FileId, LogFileSettings, LogoRRRGlobals, SearchTermGroup}
import app.logorrr.io.IoManager
import app.logorrr.util.DndUtil
import app.logorrr.views.logfiletab.LogFilePane
import app.logorrr.views.main.MainTabPane
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.input.DragEvent
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog

import java.io.File
import java.nio.file.{Files, Path}
import java.util
import java.util.stream.Collectors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.jdk.CollectionConverters.*

class LogSource(globalSearchTermGroups: ObservableList[MutSearchTermGroup]
                , settings: ObservableList[MutLogFileSettings]
                , someActiveFile: Option[FileId]
                , mainTabPane: MainTabPane) extends TinyLog:

  def getUi(): UiTarget = mainTabPane

  val ownerProperty = new SimpleObjectProperty[Window]()

  def init(owner: Window): Unit = {
    ownerProperty.set(owner)
    // sets ui to either single or multi
    mainTabPane.setOnDragOver(DndUtil.onDragAcceptAll)
    mainTabPane.setOnDragDropped(onDragDropped)
    loadSettingsFromDisc(owner)
  }

  def loadSettingsFromDisc(owner: Window): Unit = {
    if (settings.isEmpty)
      ()
    else
      val (zipSettings, fileSettings) = settings.asScala.toSeq.partition(p => p.getFileId.isZipEntry)
      val zipSettingsMap: Map[FileId, MutLogFileSettings] = zipSettings.map(s => s.getFileId -> s).toMap

      val zips: Map[FileId, Seq[FileId]] = FileId.reduceZipFiles(zipSettingsMap.keys.toSeq)

      val futures: Future[Seq[Option[LogorrrModel]]] = Future.sequence(loadZips(zips, zipSettingsMap) ++ loadFiles(fileSettings))
      val models = Await.result(futures, Duration.Inf).flatten
      models.foreach(model => {
        val p = new LogFilePane(model.mutLogFileSettings, model.entries)
        mainTabPane.addData(owner, p)
      })
      someActiveFile match {
        case Some(value) if mainTabPane.contains(value) => mainTabPane.selectFile(value)
        case _ => mainTabPane.selectLastLogFile()
      }
  }

  private def loadFiles(fileSettings: Seq[MutLogFileSettings]) = {
    fileSettings.map(lfs => Future {
      timeR({
        val entries: ObservableList[LogEntry] = IoManager.readEntries(lfs.path, if lfs.mutTimeSettings.validBinding.get() then Option(lfs.mutTimeSettings.mkImmutable()) else None)
        Option(LogorrrModel(lfs, entries))
      }, s"Loaded '${lfs.getFileId.absolutePathAsString}' from filesystem ...")
    })
  }

  private def loadZips(zips: Map[FileId, Seq[FileId]]
                       , zipSettingsMap: Map[FileId, MutLogFileSettings]): Seq[Future[Option[LogorrrModel]]] = {
    zips.keys.toSeq.flatMap(f => {
      timeR({
        IoManager.unzip(f.asPath, zips(f).toSet).map {
          // only if settings contains given fileId - user could have removed it by closing the tab - load this file
          case (fileId, entries) =>
            Future {
              if zipSettingsMap.contains(fileId) then {
                val settingz: MutLogFileSettings = zipSettingsMap(fileId)
                LogoRRRGlobals.registerSettings(settingz)
                Option(LogorrrModel(settingz, entries))
              } else None
            }
        }
      }, s"Loaded zip file '${f.absolutePathAsString}'.")
    })
  }

  def shutdown(): Unit =
    mainTabPane.shutdown()


  def onDragDropped(event: DragEvent): Unit = {
    val files = event.getDragboard.getFiles
    processDraggedFiles(files)
  }

  def processDraggedFiles(files: util.List[File]): Unit = {
    files.forEach(f => {
      val path = f.toPath
      if Files.isDirectory(path) then {
        addDirectory(path)
      } else if IoManager.isZip(path) then {
        addZip(path)
      } else {
        addFile(path)
      }
    })
  }

  def openFile(fileId: FileId): Unit = {
    if (mainTabPane.contains(fileId)) {
      mainTabPane.selectFile(fileId)
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
      SearchTermGroup("", Seq(), true)
    else
      groups.get(0).mkImmutable()
  }

  def addZip(path: Path): Unit =
    IoManager.unzip(path).foreach:
      case (fileId, entries) =>
        if !mainTabPane.contains(fileId) then
          val settings = LogFileSettings.mk(fileId, mkSearchTermGroup)
          addEntries(settings, entries)
        else mainTabPane.selectFile(fileId)

  def addFile(path: Path): Unit =
    val fileId = FileId(path)
    if !mainTabPane.contains(fileId) then
      addFileId(fileId)
    else
      mainTabPane.selectFile(fileId)

  def addFileId(fileId: FileId): Unit = timeR({
    val settings = LogFileSettings.mk(fileId, mkSearchTermGroup)
    val entries = IoManager.readEntries(settings.path, settings.someTimeSettings)
    addEntries(settings, entries)
  }, s"addFile ${fileId.absolutePathAsString}")

  private def addEntries(settings: LogFileSettings, entries: ObservableList[LogEntry]): Unit =
    val fileId = settings.fileId
    val mutLogFileSettings = LogoRRRGlobals.registerSettings(settings)
    val p = new LogFilePane(mutLogFileSettings, entries)
    mainTabPane.addData(ownerProperty.get(), p)
    mainTabPane.selectFile(fileId)

  def closeAllLogFiles(): Unit =
    mainTabPane.shutdown()
    LogoRRRGlobals.clearLogFileSettings()
