package app.logorrr.model

import app.logorrr.views.SimpleRange
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import java.time.ZoneId
import java.time.format.DateTimeFormatter

object LogEntrySetting {

  /** just my preferred time format */
  val Default = LogEntrySetting(SimpleRange(1, 24), "yyyy-MM-dd HH:mm:ss.SSS")

  implicit lazy val reader = deriveReader[LogEntrySetting]
  implicit lazy val writer = deriveWriter[LogEntrySetting]

}

case class LogEntrySetting(dateTimeRange: SimpleRange
                           , dateTimePattern: String
                           , zoneOffset: String = "+1") {

  val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneId.of(zoneOffset))
}