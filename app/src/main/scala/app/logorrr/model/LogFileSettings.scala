package app.logorrr.model

import app.logorrr.views.{Filter, Fltr}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.nio.file.{Files, Path, Paths}

object LogFileSettings {

  implicit lazy val reader = deriveReader[LogFileSettings]
  implicit lazy val writer = deriveWriter[LogFileSettings]

  val DefaultDividerPosition = 0.5
  val DefaultLogFormat: Option[LogEntryInstantFormat] = None

  def apply(p: Path): LogFileSettings =
    LogFileSettings(p.toAbsolutePath.toString,  DefaultDividerPosition, Filter.seq, DefaultLogFormat)

}


/**
 * Contains information which is necessary to display a log file.
 *
 * If there is a timestamp contained in the contained log entries, it is assumed that this
 * information is always on the same place for each log entry.
 *
 * Filters define which keywords are relevant for this given log file.
 *
 * @param pathAsString path to log file
 * @param someColumnDefinition where is
 * @param filters
 */
case class LogFileSettings(pathAsString: String
                           , dividerPosition: Double
                           , filters: Seq[Filter]
                           , someLogEntrySetting: Option[LogEntryInstantFormat]) {

  val path: Path = Paths.get(pathAsString)

  val isPathValid = Files.isReadable(path) && Files.isRegularFile(path)
}