package app.logorrr.model

import app.logorrr.conf.{DefaultSearchTermGroups, FileId, LogFileSettings, LogoRRRGlobals}
import app.logorrr.io.IoManager
import javafx.collections.ObservableList
import net.ladstatt.util.log.TinyLog

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class LogSource(defaultSearchTermGroups: DefaultSearchTermGroups) extends TinyLog:

  def loadLogFiles(settings: Seq[LogFileSettings]): Seq[LogorrrModel] =
    if (settings.isEmpty)
      Seq()
    else
      val (zipSettings, fileSettings) = settings.partition(p => p.fileId.isZipEntry)
      val zipSettingsMap: Map[FileId, LogFileSettings] = zipSettings.map(s => s.fileId -> s).toMap

      // zips is a map which contains fileIds as keys which have to be loaded, and as values their corresponding
      // settings. this is necessary as not to lose settings from previous runs
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
            Option(LogorrrModel(LogoRRRGlobals.getLogFileSettings(lfs.fileId), entries))
          }, s"Loaded '${lfs.fileId.absolutePathAsString}' from filesystem ...")
        })

        val res: Seq[Future[Option[LogorrrModel]]] = zipFutures ++ fileBasedSettings
        res

      Await.result(futures, Duration.Inf).flatten
