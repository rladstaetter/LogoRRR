package app.logorrr.model

import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
import app.logorrr.util.CanLog
import app.logorrr.views.search.Filter
import javafx.scene.paint.Color
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

import java.nio.file.{Path, Paths}
import java.time.Instant

object LogFileSettings {

  implicit lazy val reader: ConfigReader[LogFileSettings] = deriveReader[LogFileSettings]
  implicit lazy val writer: ConfigWriter[LogFileSettings] = deriveWriter[LogFileSettings]

  private val DefaultSelectedIndex = 0
  private val DefaultDividerPosition = 0.5
  private val DefaultBlockSettings = BlockSettings(10)
  private val DefaultLogFormat: Option[LogEntryInstantFormat] = None
  private val DefaultAutoScroll = false
  private val DefaultFirstViewIndex = -1
  private val DefaultLastViewIndex = -1
  private val FinestFilter: Filter = new Filter("FINEST", Color.GREY, true)
  private val InfoFilter: Filter = new Filter("INFO", Color.GREEN, true)
  private val WarningFilter: Filter = new Filter("WARNING", Color.ORANGE, true)
  private val SevereFilter: Filter = new Filter("SEVERE", Color.RED, true)

  val DefaultFilters: Seq[Filter] = Seq(FinestFilter, InfoFilter, WarningFilter, SevereFilter)

  private val DefaultFontSize = 12

  def apply(fileId: FileId): LogFileSettings = {
    LogFileSettings(fileId
      , DefaultSelectedIndex
      , Instant.now().toEpochMilli
      , DefaultDividerPosition
      , DefaultFontSize
      , DefaultFilters
      , DefaultBlockSettings
      , DefaultLogFormat
      , DefaultAutoScroll
      , DefaultFirstViewIndex
      , DefaultLastViewIndex)
  }

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
case class LogFileSettings(fileId: FileId
                           , selectedLineNumber: Int
                           , firstOpened: Long
                           , dividerPosition: Double
                           , fontSize: Int
                           , filters: Seq[Filter]
                           , blockSettings: BlockSettings
                           , someLogEntryInstantFormat: Option[LogEntryInstantFormat]
                           , autoScroll: Boolean
                           , firstVisibleTextCellIndex: Int
                           , lastVisibleTextCellIndex: Int) extends CanLog {

  val path: Path = Paths.get(fileId.value).toAbsolutePath

}