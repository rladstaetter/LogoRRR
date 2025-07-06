package app.logorrr.model

import app.logorrr.conf.BlockSettings
import app.logorrr.io.FileId
import app.logorrr.jfxbfr.{Filter, Fltr}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

import java.nio.file.Path
import java.time.Instant

object LogFileSettings {

  implicit lazy val reader: ConfigReader[LogFileSettings] = deriveReader[LogFileSettings]
  implicit lazy val writer: ConfigWriter[LogFileSettings] = deriveWriter[LogFileSettings]

  private val DefaultSelectedIndex = 0
  private val DefaultDividerPosition = 0.5
  private val DefaultBlockSettings = BlockSettings(10)
  private val DefaultLogFormat: Option[TimestampSettings] = None
  private val DefaultAutoScroll = false
  private val DefaultFirstViewIndex = -1
  private val DefaultLastViewIndex = -1
  val DefaultLowerTimestamp: Int = 0
  val DefaultUpperTimestamp: Long = Instant.now().toEpochMilli

  private val DefaultFontSize = 12

  def apply(fileId: FileId): LogFileSettings = {
    LogFileSettings(fileId
      , DefaultSelectedIndex
      , Instant.now().toEpochMilli
      , DefaultDividerPosition
      , DefaultFontSize
      , Fltr.DefaultFilters.map(f => Filter(f.getPattern, f.getColor, f.isActive))
      , DefaultBlockSettings
      , DefaultLogFormat
      , DefaultAutoScroll
      , DefaultFirstViewIndex
      , DefaultLastViewIndex
      , DefaultLowerTimestamp
      , Instant.now().toEpochMilli)
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
 * @param fileId                    wraps file reference
 * @param selectedLineNumber        which line number was selected
 * @param firstOpened               used to sort log files in tabs
 * @param dividerPosition           position of divider for this view
 * @param fontSize                  font size to use
 * @param filters                   filters which should be applied
 * @param blockSettings             settings for the left view
 * @param someTimestampSettings     used timestamp format
 * @param autoScroll                true if 'follow mode' is active
 * @param firstVisibleTextCellIndex which index is the first visible on the screen (depending on resolution, window size ...)
 * @param lastVisibleTextCellIndex  which index is the last visible on the screen (depending on resolution, window size ...)
 */
case class LogFileSettings(fileId: FileId
                           , selectedLineNumber: Int
                           , firstOpened: Long
                           , dividerPosition: Double
                           , fontSize: Int
                           , filters: Seq[Filter]
                           , blockSettings: BlockSettings
                           , someTimestampSettings: Option[TimestampSettings]
                           , autoScroll: Boolean
                           , firstVisibleTextCellIndex: Int
                           , lastVisibleTextCellIndex: Int
                           , lowerTimestamp: Long
                           , upperTimestamp: Long) {

  val path: Path = fileId.asPath.toAbsolutePath

}