package app.logorrr.model

import app.logorrr.conf.BlockSettings
import app.logorrr.util.CanLog
import app.logorrr.views.search.Filter
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.nio.file.{Files, Path, Paths}
import java.time.Instant
import scala.util.{Failure, Success, Try}

object LogFileSettings {

  implicit lazy val reader = deriveReader[LogFileSettings]
  implicit lazy val writer = deriveWriter[LogFileSettings]

  val DefaultSelectedIndex = 0
  val DefaultDividerPosition = 0.5
  val DefaultBlockSettings = BlockSettings(10)
  val DefaultLogFormat: Option[LogEntryInstantFormat] = None

  val finest: Filter = new Filter("FINEST", Color.GREY.toString)
  val info: Filter = new Filter("INFO", Color.GREEN.toString)
  val warning: Filter = new Filter("WARNING", Color.ORANGE.toString)
  val severe: Filter = new Filter("SEVERE", Color.RED.toString)

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
      , DefaultLogFormat)

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
                           , selectedIndex: Int
                           , firstOpened: Long
                           , dividerPosition: Double
                           , fontSize: Int
                           , filters: Seq[Filter]
                           , blockSettings: BlockSettings
                           , someLogEntrySetting: Option[LogEntryInstantFormat]) extends CanLog {

  val path: Path = Paths.get(pathAsString)

  val isPathValid = Files.isReadable(path) && Files.isRegularFile(path)

  def readEntries(): ObservableList[LogEntry] = {
    if (isPathValid) {
      Try(someLogEntrySetting match {
        case Some(value) => LogEntryFileReader.from(path, filters, value)
        case None => LogEntryFileReader.from(path, filters)
      }) match {
        case Success(logEntries) =>
          logInfo(s"Opening ${pathAsString} ... ")
          logEntries
        case Failure(ex) =>
          val msg = s"Could not import file ${pathAsString}"
          logException(msg, ex)
          FXCollections.observableArrayList()
      }
    } else {
      logWarn(s"Could not read $pathAsString - does it exist?")
      FXCollections.observableArrayList()
    }
  }

}