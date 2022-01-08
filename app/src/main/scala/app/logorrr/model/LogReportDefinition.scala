package app.logorrr.model

import app.logorrr.views.{Filter, LogColumnDef}

import java.nio.file.{Files, Path, Paths}

object LogReportDefinition {

  def apply(p: Path): LogReportDefinition = LogReportDefinition(p.toAbsolutePath.toString, None, Filter.seq)

  def apply(p: Path, logColumnDef: LogColumnDef): LogReportDefinition =
    LogReportDefinition(p.toAbsolutePath.toString, Option(logColumnDef), Filter.seq)

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
                               , filters: Seq[Filter]) {
  val path: Path = Paths.get(pathAsString)

  val isPathValid = Files.isReadable(path) && Files.isRegularFile(path)
}