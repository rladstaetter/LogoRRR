package app.logorrr.conf

import app.logorrr.views.logfiletab.TextConstants
import upickle.default._

import java.nio.file.Path
import java.time._

object LogFileSettings {

  implicit lazy val rw: ReadWriter[LogFileSettings] = macroRW

  private val DefaultSelectedIndex = 0
  private val DefaultDividerPosition = 0.5
  private val DefaultBlockSettings = BlockSettings(10)
  private val DefaultLogFormat: Option[TimestampSettings] = None
  private val DefaultAutoScroll = false
  private val DefaultFirstViewIndex = -1
  private val DefaultLastViewIndex = -1
  val DefaultLowerTimestamp: Int = 0
  val DefaultUpperTimestamp: Long = Instant.now().toEpochMilli
  val DefaultSearchTermGroup = Option(Settings.JavaLoggingGroup.name)

  def mk(fileId: FileId): LogFileSettings = {
    val now = Instant.now().toEpochMilli
    LogFileSettings(fileId
      , DefaultSelectedIndex
      , now
      , DefaultDividerPosition
      , TextConstants.DefaultFontSize
      , Settings.JavaLoggingGroup.terms
      , DefaultBlockSettings
      , DefaultLogFormat
      , DefaultAutoScroll
      , DefaultFirstViewIndex
      , DefaultLastViewIndex
      , DefaultLowerTimestamp
      , now
      , DefaultSearchTermGroup
      , LogoRRRGlobals.getSettings.searchTermGroups)
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
 * @param fileId                      wraps file reference
 * @param selectedLineNumber          which line number was selected
 * @param firstOpened                 used to sort log files in tabs
 * @param dividerPosition             position of divider for this view
 * @param fontSize                    font size to use
 * @param searchTerms                 elements to be searched for, with their coloring and activation
 * @param blockSettings               settings for the left view
 * @param someTimestampSettings       used timestamp format
 * @param autoScroll                  true if 'follow mode' is active
 * @param firstVisibleTextCellIndex   which index is the first visible on the screen (depending on resolution, window size ...)
 * @param lastVisibleTextCellIndex    which index is the last visible on the screen (depending on resolution, window size ...)
 * @param someSelectedSearchTermGroup selected search term group, if any
 */
case class LogFileSettings(fileId: FileId
                           , selectedLineNumber: Int
                           , firstOpened: Long
                           , dividerPosition: Double
                           , fontSize: Int
                           , searchTerms: Seq[SearchTerm]
                           , blockSettings: BlockSettings
                           ,  someTimestampSettings: Option[TimestampSettings] = None
                           , autoScroll: Boolean
                           , firstVisibleTextCellIndex: Int
                           , lastVisibleTextCellIndex: Int
                           , lowerTimestamp: Long
                           , upperTimestamp: Long
                           , someSelectedSearchTermGroup: scala.Option[String]
                           , searchTermGroups: Map[String, Seq[SearchTerm]]) {

  lazy val path: Path = fileId.asPath.toAbsolutePath

}
