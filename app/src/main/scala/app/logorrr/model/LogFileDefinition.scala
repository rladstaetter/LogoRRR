package app.logorrr.model

import app.logorrr.views.Filter
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.nio.file.{Files, Path, Paths}

object LogFileDefinition {

  implicit lazy val reader = deriveReader[LogFileDefinition]
  implicit lazy val writer = deriveWriter[LogFileDefinition]

  val DefaultDividerPosition = 0.5
  val defaultActive = false
  val DefaultLogFormat: Option[LogEntrySetting] = None

  def apply(p: Path): LogFileDefinition =
    LogFileDefinition(p.toAbsolutePath.toString, defaultActive, DefaultDividerPosition, Filter.seq, DefaultLogFormat)

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
case class LogFileDefinition(pathAsString: String
                             , @deprecated active: Boolean // use activeLogReport from RecentFileSettings
                             , dividerPosition: Double
                             , filters: Seq[Filter]
                             , someLogEntrySetting: Option[LogEntrySetting]) {

  val path: Path = Paths.get(pathAsString)

  val isPathValid = Files.isReadable(path) && Files.isRegularFile(path)
}