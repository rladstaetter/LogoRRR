package app.logorrr.conf

import pureconfig.generic.semiauto.{deriveReader, deriveWriter}
import pureconfig.{ConfigReader, ConfigWriter}

object SimpleRange {

  implicit lazy val reader: ConfigReader[SimpleRange] = deriveReader[SimpleRange]
  implicit lazy val writer: ConfigWriter[SimpleRange] = deriveWriter[SimpleRange]

}

case class SimpleRange(start: Int, end: Int) {
  require(start <= end, s"Expected start <= end, but was $start <= $end")
  val length: Int = end - start
}