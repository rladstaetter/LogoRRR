package app.logorrr.conf

import app.logorrr.util.HLink
import app.logorrr.views.a11y.uinodes.UiNodes
import net.ladstatt.util.log.TinyLog
import upickle.default.*

import java.time.format.DateTimeFormatter

object TimeSettings extends TinyLog:

  val DefaultPattern = "yyyy-MM-dd HH:mm:ss.SSS"
  val DefaultStartCol = 1
  val DefaultEndCol = 24

  val dateFormatterHLink = HLink(UiNodes.OpenDateFormatterSite, "https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/time/format/DateTimeFormatter.html", "pattern documentation")

  val DefaultFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DefaultPattern)

  val Default: TimeSettings = TimeSettings(DefaultStartCol, DefaultEndCol, DefaultPattern)

  val Invalid = TimeSettings(0, 0, "")

case class TimeSettings(startCol: Int
                        , endCol: Int
                        , dateTimePattern: String) derives ReadWriter:

  def isValid: Boolean = startCol < endCol && dateTimePattern.nonEmpty
