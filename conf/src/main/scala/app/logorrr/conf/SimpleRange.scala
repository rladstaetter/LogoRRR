package app.logorrr.conf

import pureconfig.*
import pureconfig.generic.derivation.default.*
import pureconfig.generic.auto.*

object SimpleRange {

  implicit lazy val reader: ConfigReader[SimpleRange] = ConfigReader.derived[SimpleRange]
 // implicit lazy val writer: ConfigWriter[SimpleRange] = deriveWriter[SimpleRange]

}

case class SimpleRange(start: Int, end: Int) {
  require(start <= end)
}