package app.logorrr.model

import app.logorrr.conf.BlockSettings
import app.logorrr.util.{CanLog, OsUtil}
import app.logorrr.views.search.Filter
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

import java.nio.file.{Files, Path, Paths}
import java.time.Instant
import scala.util.{Failure, Success, Try}

object LogFileSettings {

  implicit lazy val reader: ConfigReader[LogFileSettings] = deriveReader[LogFileSettings]
  implicit lazy val writer: ConfigWriter[LogFileSettings] = deriveWriter[LogFileSettings]

  private val DefaultSelectedIndex = 0
  private val DefaultDividerPosition = 0.5
  private val DefaultBlockSettings = BlockSettings(10)
  private val DefaultLogFormat: Option[LogEntryInstantFormat] = None
  private val DefaultAutoScroll = false

  private val finest: Filter = new Filter("FINEST", Color.GREY, true)
  private val info: Filter = new Filter("INFO", Color.GREEN, true)
  private val warning: Filter = new Filter("WARNING", Color.ORANGE, true)
  private val severe: Filter = new Filter("SEVERE", Color.RED, true)

  val DefaultFilter: Seq[Filter] = Seq(finest, info, warning, severe)
  val DefaultFontSize = 12

  def apply(p: Path): LogFileSettings =
    LogFileSettings(p.toAbsolutePath.toString
      , DefaultSelectedIndex
      , Instant.now().toEpochMilli
      , DefaultDividerPosition
      , DefaultFontSize
      , DefaultFilter
      , DefaultBlockSettings
      , DefaultLogFormat
      , DefaultAutoScroll)

}


/**
 * Contains information which is necessary to display a log file.
 *
 * If there is a timestamp contained in the contained log entries, it is assumed that this
 * information is always on the same place for each log entry.
 *
 * Filters define which keywords are relevant for this given log file.
 *
 */
case class LogFileSettings(pathAsString: String
                           , selectedLineNumber: Int
                           , firstOpened: Long
                           , dividerPosition: Double
                           , fontSize: Int
                           , filters: Seq[Filter]
                           , blockSettings: BlockSettings
                           , someLogEntryInstantFormat: Option[LogEntryInstantFormat]
                           , autoScroll: Boolean) extends CanLog {

  val path: Path = Paths.get(pathAsString).toAbsolutePath

  val isPathValid: Boolean =
    if (OsUtil.isMac) {
      Files.exists(path)
    } else {
      // without security bookmarks initialized, this returns false on mac
      Files.isReadable(path) && Files.isRegularFile(path)
    }

  def readEntries(): ObservableList[LogEntry] = {
    if (isPathValid) {
      Try(someLogEntryInstantFormat match {
        case Some(instantFormat) => LogEntryFileReader.from(path,  instantFormat)
        case None => LogEntryFileReader.from(path)
      }) match {
        case Success(logEntries) =>
          logEntries
        case Failure(ex) =>
          val msg = s"Could not load file $pathAsString"
          logException(msg, ex)
          FXCollections.observableArrayList()
      }
    } else {
      logWarn(s"Could not read $pathAsString - does it exist?")
      FXCollections.observableArrayList()
    }
  }

}