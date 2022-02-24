package app.logorrr.model

import app.logorrr.views.{Filter, LogColumnDef, SimpleRange}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.nio.file.{Files, Path, Paths}

object LogReportDefinition {

  implicit lazy val reader = deriveReader[LogReportDefinition]
  implicit lazy val writer = deriveWriter[LogReportDefinition]

  val defaultDividerPosition = 0.5
  val defaultActive = false

  def apply(p: Path): LogReportDefinition = LogReportDefinition(p.toAbsolutePath.toString, None, defaultActive, defaultDividerPosition, Filter.seq, None)

  def apply(p: Path, logColumnDef: LogColumnDef): LogReportDefinition =
    LogReportDefinition(p.toAbsolutePath.toString, Option(logColumnDef), defaultActive, defaultDividerPosition, Filter.seq, None)

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
case class LogReportDefinition(pathAsString: String
                               , someColumnDefinition: Option[LogColumnDef] = None
                               , @deprecated active: Boolean // use activeLogReport from RecentFileSettings
                               , dividerPosition: Double
                               , filters: Seq[Filter]
                               , someLogEntrySetting: Option[LogEntrySetting]) {

  val path: Path = Paths.get(pathAsString)

  val isPathValid = Files.isReadable(path) && Files.isRegularFile(path)
}