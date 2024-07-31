package app.logorrr.views.settings.timestamp

import pureconfig.{ConfigReader, ConfigWriter}
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object SimpleRange {

  implicit lazy val reader: ConfigReader[SimpleRange] = deriveReader[SimpleRange]
  implicit lazy val writer: ConfigWriter[SimpleRange] = deriveWriter[SimpleRange]

}

case class SimpleRange(start: Int, end: Int) {
  require(start <= end)
}