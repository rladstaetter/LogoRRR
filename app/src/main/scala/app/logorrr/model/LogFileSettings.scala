package app.logorrr.model

import app.logorrr.conf.BlockSettings
import app.logorrr.views.{Filter, Fltr}
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.nio.file.{Files, Path, Paths}

object LogFileSettings {

  implicit lazy val reader = deriveReader[LogFileSettings]
  implicit lazy val writer = deriveWriter[LogFileSettings]

  val DefaultDividerPosition = 0.5
  val DefaultBlockSettings = BlockSettings(10)
  val DefaultLogFormat: Option[LogEntryInstantFormat] = Option(LogEntryInstantFormat.Default)

  val finest: Filter = new Filter("FINEST", Color.GREY.toString)
  val info: Filter = new Filter("INFO", Color.GREEN.toString)
  val warning: Filter = new Filter("WARNING", Color.ORANGE.toString)
  val severe: Filter = new Filter("SEVERE", Color.RED.toString)

  val DefaultFilter: Seq[Filter] = Seq(finest, info, warning, severe)

  def apply(p: Path): LogFileSettings =
    LogFileSettings(p.toAbsolutePath.toString, DefaultDividerPosition, DefaultFilter, DefaultBlockSettings, DefaultLogFormat)

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
                           , blockSettings: BlockSettings
                           , someLogEntrySetting: Option[LogEntryInstantFormat]) {

  val path: Path = Paths.get(pathAsString)

  val isPathValid = Files.isReadable(path) && Files.isRegularFile(path)
}