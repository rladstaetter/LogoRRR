package app.logorrr.views.main

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.{FileId, IoManager}
import app.logorrr.model.LogFileSettings
import app.logorrr.services.fileservices.LogoRRRFileOpenService
import app.logorrr.util.CanLog
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.layout.BorderPane

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class LogoRRRMain(closeStage: => Unit
                 , logoRRRFileOpenService: LogoRRRFileOpenService
                 , isUnderTest : Boolean) extends BorderPane with CanLog {

  private val mainTabPane = new MainTabPane

  val bar = new MainMenuBar(logoRRRFileOpenService, openFile, closeAllLogFiles(), closeStage, isUnderTest)

  def getLogFileTabs: mutable.Seq[LogFileTab] = mainTabPane.getLogFileTabs

  def init(): Unit = {
    setTop(bar)
    setCenter(mainTabPane)
    mainTabPane.init()
  }

  def initLogFilesFromConfig(): Unit = {
    val entries = LogoRRRGlobals.getOrderedLogFileSettings
    if (entries.nonEmpty) {
      loadLogFiles(LogoRRRGlobals.getOrderedLogFileSettings)
    } else {
      logTrace("No log files loaded.")
    }
  }

  private def loadLogFiles(settings: Seq[LogFileSettings]): Unit = {
    val (zipSettings, fileSettings) = settings.partition(p => p.fileId.isZipEntry)
    val zipSettingsMap: Map[FileId, LogFileSettings] = zipSettings.map(s => s.fileId -> s).toMap
    // zips is a map which contains fileIds as keys which have to be loaded, and as values their corresponding
    // settings. this is necessary as not to lose settings from previous runs
    val zips: Map[FileId, Seq[FileId]] = FileId.reduceZipFiles(zipSettingsMap.keys.toSeq)

    val futures: Future[Seq[Option[LogFileTab]]] = Future.sequence {

      // load zip files also in parallel
      val zipFutures: Seq[Future[Option[LogFileTab]]] =
        zips.keys.toSeq.flatMap(f => {
          timeR({
            IoManager.unzip(f.asPath, zips(f).toSet).map {
              // only if settings contains given fileId - user could have removed it by closing the tab - load this file
              case (fileId, entries) =>
                Future {
                  if (zipSettingsMap.contains(fileId)) {
                    Option(mainTabPane.addEntriesFromZip(zipSettingsMap(fileId), entries))
                  } else None
                }
            }
          }, s"Loaded zip file '${f.absolutePathAsString}'.")
        })

      val fileBasedSettings: Seq[Future[Option[LogFileTab]]] = fileSettings.map(lfs => Future {
        timeR({
          val entries = IoManager.readEntries(lfs.path, lfs.someLogEntryInstantFormat)
          val tab = LogFileTab(LogoRRRGlobals.getLogFileSettings(lfs.fileId), entries)
          mainTabPane.addLogFileTab(tab)
          Option(tab)
        }, s"Loaded '${lfs.fileId.absolutePathAsString}' from filesystem ...")
      })

      zipFutures ++ fileBasedSettings
    }
    val logFileTabs: Seq[LogFileTab] = Await.result(futures, Duration.Inf).flatten
    logTrace("Loaded " + logFileTabs.size + " files ... ")

    // only after loading all files we initialize the 'add' listener
    // otherwise we would overwrite the active log everytime
    mainTabPane.initSelectionListener()

  }

  def contains(fileId: FileId): Boolean = mainTabPane.contains(fileId)

  /** called when 'Open File' from the main menu bar is selected. */
  def openFile(fileId: FileId): Unit = {
    if (IoManager.isZip(fileId.asPath)) {
      mainTabPane.openZipFile(fileId.asPath)
    } else if (contains(fileId)) {
      mainTabPane.selectFile(fileId).recalculateChunkListViewAndScrollToActiveElement()
    } else {
      mainTabPane.addFile(fileId)
    }
  }

  /** removes all log files */
  def closeAllLogFiles(): Unit = {
    shutdown()
    LogoRRRGlobals.clearLogFileSettings()
  }

  def selectLog(fileId: FileId): LogFileTab = mainTabPane.selectFile(fileId)

  def selectLastLogFile(): Unit = mainTabPane.selectLastLogFile()

  def shutdown(): Unit = mainTabPane.shutdown()

}
